package com.mvv.bank.orders.domain

import com.mvv.bank.util.checkInitialized
import java.math.BigDecimal
import java.time.ZonedDateTime


sealed class AbstractFxCashOrder : AbstractOrder<Currency, Quote>() {

    lateinit var buyCurrency: Currency
    lateinit var sellCurrency: Currency

    // It also can be used to set resultingPrice/resultingQuote from FX rate during order execution (if they are not set).
    // It is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost).
    var resultingRate: FxRate? = null
        set(value) {
            field = value
            if (value != null && resultingPrice == null) {
                // TODO: test with inverted rate
                resultingPrice = value.asPrice(priceCurrency, buySellType)
            }
            if (value != null && resultingQuote == null) {
                // TODO: test with inverted rate
                resultingQuote = FxRateAsQuote(value, priceCurrency)
            }
        }

    val priceCurrency: Currency get() = when (buySellType) {
        // opposite
        //null -> null
        BuySellType.BUY  -> sellCurrency
        BuySellType.SELL -> buyCurrency
    }

    @Deprecated("Better to use buyCurrency/sellCurrency directly if you access/config cash order by direct FxCashOrder typed variable.")
    override var product: Currency
        get() = when (buySellType) {
            //null -> null
            BuySellType.BUY  -> buyCurrency
            BuySellType.SELL -> sellCurrency
        }
        set(value) {
            val buySellType = this.buySellType
            checkInitialized(::buySellType) { "buySellType should be set before setting product." }
            when (buySellType) {
                BuySellType.BUY  -> buyCurrency  = value
                BuySellType.SELL -> sellCurrency = value
            }
        }

    fun toExecute(rate: FxRate): Boolean = toExecute(FxRateAsQuote(rate, priceCurrency))
}


class FxCashLimitOrder private constructor() : AbstractFxCashOrder(), LimitOrder<Currency, Quote> {

    private val limitOrderSupport = StopLimitOrderSupport(this, ::limitPrice, ::dailyExecutionType)

    override val orderType: OrderType = OrderType.LIMIT_ORDER
    override lateinit var limitPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType

    override fun validateCurrentState() {
        super.validateCurrentState()
        limitOrderSupport.validateCurrentState()
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)
        limitOrderSupport.validateNextState(nextState)
    }

    override fun toExecute(quote: Quote): Boolean = limitOrderSupport.toExecute(quote)

    companion object {
        fun create(
            id: Long? = null,
            side: Side,
            buySellType: BuySellType,
            buyCurrency: Currency,
            sellCurrency: Currency,
            volume: BigDecimal,
            limitPrice: Amount,
            dailyExecutionType: DailyExecutionType,

            marketSymbol: String,
            market: Market,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt:   ZonedDateTime? = null,
            executedAt: ZonedDateTime? = null,
            canceledAt: ZonedDateTime? = null,
            expiredAt:  ZonedDateTime? = null,

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
            order.volume = volume
            order.limitPrice   = limitPrice
            order.dailyExecutionType = dailyExecutionType

            order.marketSymbol = marketSymbol
            order.market = market

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote
            order.resultingRate = resultingRate

            order.validateCurrentState()

            return order
        }
    }
}


class FxCashStopOrder private constructor() : AbstractFxCashOrder(), StopOrder<Currency, Quote> {

    private val stopOrderSupport = StopLimitOrderSupport(this, ::stopPrice, ::dailyExecutionType)

    override val orderType: OrderType = OrderType.STOP_ORDER
    override lateinit var stopPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType

    override fun validateCurrentState() {
        super.validateCurrentState()
        stopOrderSupport.validateCurrentState()
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)
        stopOrderSupport.validateNextState(nextState)
    }

    override fun toExecute(quote: Quote): Boolean = stopOrderSupport.toExecute(quote)

    companion object {
        fun create(
            id: Long? = null,
            side: Side,
            buySellType: BuySellType,
            buyCurrency: Currency,
            sellCurrency: Currency,
            volume: BigDecimal,
            stopPrice: Amount,
            dailyExecutionType: DailyExecutionType,

            marketSymbol: String,
            market: Market,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt:   ZonedDateTime? = null,
            executedAt: ZonedDateTime? = null,
            canceledAt: ZonedDateTime? = null,
            expiredAt:  ZonedDateTime? = null,

            resultingRate: FxRate? = null,
            resultingPrice: Amount? = null,
            resultingQuote: Quote? = null,
        ): FxCashStopOrder {
            val order = FxCashStopOrder()
            order.id = id

            order.side  = side
            order.buySellType  = buySellType
            order.buyCurrency  = buyCurrency
            order.sellCurrency = sellCurrency
            order.volume = volume
            order.stopPrice = stopPrice
            order.dailyExecutionType = dailyExecutionType

            order.marketSymbol = marketSymbol
            order.market = market

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote
            // ! should be last because has side effect !
            order.resultingRate = resultingRate

            order.validateCurrentState()

            return order
        }
    }
}


class FxCashMarketOrder private constructor() : AbstractFxCashOrder() {
    override val orderType: OrderType = OrderType.MARKET_ORDER

    override fun toExecute(quote: Quote): Boolean {
        val rateCurrencyPair = CurrencyPair.of(Currency.of(quote.productSymbol), quote.bid.currency)
        val orderCurrencyPair = CurrencyPair.of(buyCurrency, sellCurrency)

        check(rateCurrencyPair == orderCurrencyPair || rateCurrencyPair == orderCurrencyPair.inverted()) {
            "FX rate currencies $rateCurrencyPair does not suite order currencies $orderCurrencyPair." }
        return true
    }

    companion object {
        fun create(
            id: Long? = null,
            side: Side,
            buySellType: BuySellType,
            buyCurrency: Currency,
            sellCurrency: Currency,
            volume: BigDecimal,

            marketSymbol: String,
            market: Market,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt:   ZonedDateTime? = null,
            executedAt: ZonedDateTime? = null,
            canceledAt: ZonedDateTime? = null,
            expiredAt:  ZonedDateTime? = null,

            resultingRate: FxRate? = null,
            resultingPrice: Amount? = null,
            resultingQuote: Quote? = null,
        ): FxCashMarketOrder {
            val order = FxCashMarketOrder()
            order.id = id

            order.side  = side
            order.buySellType  = buySellType
            order.buyCurrency  = buyCurrency
            order.sellCurrency = sellCurrency
            order.volume = volume

            order.marketSymbol = marketSymbol
            order.market = market

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote
            order.resultingRate = resultingRate

            order.validateCurrentState()

            return order
        }
    }
}
