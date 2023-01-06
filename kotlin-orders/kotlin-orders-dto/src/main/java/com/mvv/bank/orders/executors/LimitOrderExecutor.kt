package com.mvv.bank.orders.executors

import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.repository.CashLimitOrderRepository
import com.mvv.bank.log.safe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.*

private val log: Logger = LoggerFactory.getLogger(CashLimitOrderExecutor::class.java)

// TODO: refactor to use start/stop/close
class CashLimitOrderExecutor (
    private val dateTimeService: DateTimeService,
    private val orderRepository: CashLimitOrderRepository,
    private val market: Market,
) : CashOrderExecutor {
    private val orders : MutableList<CashLimitOrder> = CopyOnWriteArrayList()
    private val executor : ExecutorService = Executors.newFixedThreadPool(10) // TODO: move to configuration
    private val processedOrders: BlockingQueue<CashLimitOrder> = LinkedBlockingQueue(32768)
    private val processedOrdersSavingThread = Thread({}, "Processed orders saver")

    override fun priceChanged(price: FxRate) {

        val ordersToExecute = orders.asSequence()
            .filter { price.currencyPair.containsCurrencies(it.buyCurrency, it.sellCurrency) }
            .filter { it.toExecute(price) }
            .toList()

        ordersToExecute.forEach {
            executor.submit { executeOrder(it, price) }
        }
    }

    private fun executeOrder(order: CashLimitOrder, currentPrice: FxRate) {
        try {
            executeOrderOnMarket(order, currentPrice)
            orderExecuted(order, currentPrice)
        }
        catch (ex: Exception) {
            log.error("Error of executing orders ({}).", ex.message.safe, ex)
        }
    }

    private val context: OrderContext get() =
        OrderContext.create(dateTimeService = dateTimeService, market = market)

    private fun orderExecuted(order: CashLimitOrder, currentPrice: FxRate) {
        order.changeOrderState(OrderState.EXECUTED, context)
        order.resultingRate = currentPrice

        processedOrders.add(order)
        TODO("Not yet implemented")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun executeOrderOnMarket(order: CashLimitOrder, currentPrice: FxRate) {
        // T O D O: how to emulate it?
        // TODO: introduce service which can be substituted in tests
        //
        Thread.sleep(ThreadLocalRandom.current().nextLong(500))
    }

    private fun saveProcessedOrders() {
        val batchSize = 10 // TODO: move to configuration
        //val processedPart = mutableListOf<LimitOrder>()
        val processedPart = ArrayList<CashLimitOrder>(batchSize)

        // blocking call
        val firstOrder = processedOrders.take()

        // drainTo is not blocking!
        processedOrders.drainTo(processedPart, batchSize - 1)
        processedPart.add(0, firstOrder)

        saveOrders(processedPart)
    }

    private fun saveOrders(processed: List<CashLimitOrder>) {
        orderRepository.saveOrders(processed)
    }
}
