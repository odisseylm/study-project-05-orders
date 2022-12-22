package com.mvv.bank.orders.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

private val log: Logger = LoggerFactory.getLogger(AbstractLimitOrder::class.java)


abstract class AbstractLimitOrder<Product, Quote> : AbstractOrder<Product, Quote>() {

    override val orderType: OrderType = OrderType.LIMIT_ORDER
    var limitPrice: Amount? = null
    var executionType: ExecutionType? = null

    //abstract fun toExecute(currentPrice: FxRate): Boolean

    open fun toExecute(quote: com.mvv.bank.orders.domain.Quote): Boolean {
        val buySellType = this.buySellType
        val limitPrice  = this.limitPrice

        checkNotNull(buySellType) { "Buy/Sell type is not set for order [${id}]." }
        checkNotNull(limitPrice)  { "Limit price is not set for order [${id}]."   }

        check(limitPrice.currency == quote.bid.currency) {
            "Quote $quote has incorrect currency ${quote.bid.currency}." }
        check(limitPrice.currency == quote.ask.currency) {
            "Quote $quote has incorrect currency ${quote.bid.currency}." }

        return when (buySellType) {
            BuySellType.BUY  -> quote.bid.amount <= limitPrice.amount
            BuySellType.SELL -> quote.ask.amount >= limitPrice.amount
        }
    }

    // TODO: find better name
    enum class ExecutionType (val humanName: String) {
        DAY_ONLY("Day Only"),
        GTC("Good 'til Canceled"),
    }

    override fun validateCurrentState() {
        super.validateCurrentState()

        if (orderState == OrderState.UNKNOWN) {
            return
        }

        checkNotNull(limitPrice)
        checkNotNull(executionType)
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)

        if (orderState == OrderState.UNKNOWN) {
            return
        }

        checkNotNull(limitPrice)
        checkNotNull(executionType)
    }
}


class FxCashLimitOrder private constructor() : AbstractLimitOrder<Currency, Quote>() {

    @Suppress("MemberVisibilityCanBePrivate")
    var buyCurrency: Currency? = null
    @Suppress("MemberVisibilityCanBePrivate")
    var sellCurrency: Currency? = null

    // can be used to set resultingPrice from FX rate during order execution
    // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
    var resultingRate: FxRate? = null
        set(value) {
            field = value
            if (value != null && resultingPrice == null) {
                resultingPrice = value.asPrice(priceCurrency!!, buySellType!!)
                resultingQuote = FxRateAsQuote(value, priceCurrency!!)
            }
        }

    private val priceCurrency: Currency? get() = if (buySellType == BuySellType.BUY) buyCurrency else sellCurrency

    override var product: Currency?
        get() = if (buySellType == BuySellType.BUY) buyCurrency else sellCurrency
        @Deprecated("Better to use buyCurrency/sellCurrency directly.")
        set(value) { if (buySellType == BuySellType.BUY) buyCurrency = value else sellCurrency = value }

    fun toExecute(rate: FxRate): Boolean = toExecute(FxRateAsQuote(rate, priceCurrency!!))


    companion object {
        fun create(
            id: Long? = null,
            buySellType: BuySellType,
            buyCurrency: Currency,
            sellCurrency: Currency,
            limitPrice: Amount,
            executionType: ExecutionType,

            market: Market,
            orderState: OrderState = OrderState.UNKNOWN,

            placedAt: Instant? = null,
            executedAt: Instant? = null,
            canceledAt: Instant? = null,
            expiredAt: Instant? = null,

            resultingRate: FxRate? = null,
            resultingPrice: Amount? = null,
            resultingQuote: Quote? = null,
        ): FxCashLimitOrder {
            val order = FxCashLimitOrder()
            order.id = id

            order.buySellType = buySellType
            order.buyCurrency = buyCurrency
            order.sellCurrency = sellCurrency
            order.limitPrice = limitPrice
            order.executionType = executionType

            order.market = market

            order.orderState = orderState

            order.placedAt = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote
            if (resultingRate != null && resultingPrice == null) {
                order.resultingPrice = resultingRate.asPrice(order.priceCurrency!!, buySellType)
            }
            if (resultingRate != null && resultingQuote == null) {
                order.resultingQuote = FxRateAsQuote(resultingRate, order.priceCurrency!!)
            }

            order.validateCurrentState()

            return order
        }
    }
}


//class StockLimitOrder : AbstractLimitOrder<Company, StockQuote> (
//
//)
