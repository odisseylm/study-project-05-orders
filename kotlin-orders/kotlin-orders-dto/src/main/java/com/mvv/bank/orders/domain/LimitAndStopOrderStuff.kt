package com.mvv.bank.orders.domain

import com.mvv.bank.util.checkInitialized
import com.mvv.bank.util.checkPropertyInitialized
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty
import com.mvv.bank.orders.domain.Quote as BaseQuote

@Suppress("unused")
private val log: Logger = LoggerFactory.getLogger(LimitOrder::class.java)


interface LimitOrder<Product: Any, Quote: BaseQuote> : Order<Product, Quote> {
    var limitPrice: Amount
    var dailyExecutionType: DailyExecutionType
}

interface StopOrder<Product: Any, Quote: BaseQuote> : Order<Product, Quote> {
    var stopPrice: Amount
    var dailyExecutionType: DailyExecutionType
}


/** Since java does not support multiple class inheritance common logic for limit and stop orders are put there. */
internal class StopLimitOrderSupport<Order: com.mvv.bank.orders.domain.Order<*, *>>(
    private val order: Order,

    private val limitStopPricePropName: String,
    private val limitStopPrice: () -> Amount,

    private val dailyExecutionTypePropName: String = "dailyExecutionType",
    private val dailyExecutionType: () -> DailyExecutionType,

    ) {

    constructor (
        order: Order,
        limitStopPrice: KProperty<Amount>,
        dailyExecutionType: KProperty<DailyExecutionType>,
        ) : this(order, limitStopPrice.name, { limitStopPrice.getter.call() }, dailyExecutionType.name, { dailyExecutionType.getter.call() })

    fun toExecute(quote: BaseQuote): Boolean {
        val buySellType = order.buySellType
        val limitPrice = limitStopPrice()

        checkInitialized({ buySellType }) { "Buy/Sell type is not set for order [${order.id}]." }
        checkInitialized({ limitPrice }) { "Limit price is not set for order [${order.id}]." }

        check(limitPrice.currency == quote.bid.currency) {
            "Quote $quote has incorrect currency ${quote.bid.currency}." }
        check(limitPrice.currency == quote.ask.currency) {
            "Quote $quote has incorrect currency ${quote.bid.currency}." }

        // For Stock Exchange:
        //  bid - the highest price a buyer (dealer/bank/market) will pay to buy a specified number of shares of a stock
        //  ask - the lowest price at which a seller (dealer/bank/market) will sell the stock
        // The bid price will almost always be lower than the ask or “offer,” price.
        // The market sets bid and ask prices through the placement of buy and sell orders placed by investors,
        // and/or market-makers. If buying demand exceeds selling supply,
        // then often the stock price will rise in the short-term, although that is not guaranteed.
        // Example: bid=$9.95 ask=$10.05
        // See:
        //  https://www.investopedia.com/ask/answers/042215/what-do-bid-and-ask-prices-represent-stock-quote.asp
        //  https://www.investopedia.com/terms/b/bid-and-ask.asp
        //  https://corporatefinanceinstitute.com/resources/equities/bid-and-ask/
        //
        //
        // In Foreign Exchange:
        //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
        //  ask - price of client 'buy'  (and dealer/bank 'sell')

        return when (order.side) {
            //null -> throw IllegalStateException("Order side is not set.")
            Side.CLIENT -> when (buySellType) {
                BuySellType.SELL -> quote.bid.value >= limitPrice.value
                BuySellType.BUY -> quote.ask.value <= limitPrice.value
            }

            Side.BANK_MARKET -> when (buySellType) {
                BuySellType.SELL -> quote.ask.value >= limitPrice.value
                BuySellType.BUY -> quote.bid.value <= limitPrice.value
            }
        }
    }

    fun validateCurrentState() = validateCurrentStopLimitAttributes()
    @Suppress("UNUSED_PARAMETER")
    fun validateNextState(nextState: OrderState) = validateCurrentStopLimitAttributes()

    private fun validateCurrentStopLimitAttributes() {
        if (order.orderState == OrderState.UNKNOWN) {
            return
        }

        checkPropertyInitialized(limitStopPricePropName, limitStopPrice)
        checkPropertyInitialized(dailyExecutionTypePropName, dailyExecutionType)
    }
}
