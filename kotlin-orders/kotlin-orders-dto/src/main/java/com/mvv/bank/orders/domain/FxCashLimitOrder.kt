package com.mvv.bank.orders.domain

import java.time.Instant

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
                // TODO: test with inverted rate
                resultingPrice = value.asPrice(priceCurrency!!, buySellType!!)
                resultingQuote = FxRateAsQuote(value, priceCurrency!!)
            }
        }

    private val priceCurrency: Currency? get() = when (buySellType) {
        // opposite
        null -> null
        BuySellType.BUY  -> sellCurrency
        BuySellType.SELL -> buyCurrency
    }

    override var product: Currency?
        get() = when (buySellType) {
            null -> null
            BuySellType.BUY  -> buyCurrency
            BuySellType.SELL -> sellCurrency
        }
        @Deprecated("Better to use buyCurrency/sellCurrency directly.")
        set(value) {
            val buySellType = this.buySellType
            checkNotNull(buySellType) { "buySellType should be set before setting product." }
            when (buySellType) {
                BuySellType.BUY  -> buyCurrency  = value
                BuySellType.SELL -> sellCurrency = value
            }
        }

    fun toExecute(rate: FxRate): Boolean = toExecute(FxRateAsQuote(rate, priceCurrency!!))


    companion object {
        fun create(
            id: Long? = null,
            side: Side,
            buySellType: BuySellType,
            buyCurrency: Currency,
            sellCurrency: Currency,
            limitPrice: Amount,
            dailyExecutionType: DailyExecutionType,

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

            order.side  = side
            order.buySellType  = buySellType
            order.buyCurrency  = buyCurrency
            order.sellCurrency = sellCurrency
            order.limitPrice   = limitPrice
            order.dailyExecutionType = dailyExecutionType

            order.market = market

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote
            if (resultingRate != null && resultingPrice == null) {
                // TODO: test with inverted rate
                order.resultingPrice = resultingRate.asPrice(order.priceCurrency!!, buySellType)
            }
            if (resultingRate != null && resultingQuote == null) {
                // TODO: test with inverted rate
                order.resultingQuote = FxRateAsQuote(resultingRate, order.priceCurrency!!)
            }

            order.validateCurrentState()

            return order
        }
    }
}