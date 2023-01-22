package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe
import com.mvv.bank.util.checkInitialized
import java.math.BigDecimal
import java.time.ZonedDateTime


sealed class AbstractCashOrder : AbstractOrder<Currency, Quote>() {

    class Base (
        override val id: Long? = null,
        override val user: User,
        override val side: Side,
        override val buySellType: BuySellType,
        val buyCurrency: Currency,
        val sellCurrency: Currency,
        override val volume: BigDecimal,

        override val market: Market,

        override val orderState: OrderState = OrderState.UNKNOWN,

        override val placedAt:   ZonedDateTime? = null,
        override val executedAt: ZonedDateTime? = null,
        override val canceledAt: ZonedDateTime? = null,
        override val expiredAt:  ZonedDateTime? = null,

        val resultingRate: FxRate? = null,
        override val resultingPrice: Amount? = null,
        override val resultingQuote: Quote? = null,

        ) : _BaseAttrs<Currency, Quote>() {
        fun copyToOrder(order: AbstractCashOrder) {
            super.copyToOrder(order)

            order.buyCurrency  = buyCurrency
            order.sellCurrency = sellCurrency

            order.resultingRate = resultingRate
        }
    }

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



class CashLimitOrder private constructor() : AbstractCashOrder(), LimitOrder<Currency, Quote> {

    private val limitOrderSupport = StopLimitOrderSupport(this, ::limitPrice, ::dailyExecutionType)

    override val orderType: OrderType = OrderType.LIMIT_ORDER
    override lateinit var limitPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType

    override fun validateCurrentState() {
        super.validateCurrentState()
        limitOrderSupport.validateCurrentState()

        check(limitPrice.currency == priceCurrency) {
            "Limit price currency (${limitPrice.currency.safe}) differs from price currency (${priceCurrency.safe})." }
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)
        limitOrderSupport.validateNextState(nextState)
    }

    override fun toExecute(quote: Quote): Boolean = limitOrderSupport.toExecute(quote)

    companion object {
        fun create(
            base: Base,
            limitPrice: Amount,
            dailyExecutionType: DailyExecutionType,
        ): CashLimitOrder {
            val order = CashLimitOrder()
            base.copyToOrder(order)

            order.limitPrice   = limitPrice
            order.dailyExecutionType = dailyExecutionType

            order.validateCurrentState()
            return order
        }
    }
}


class CashStopOrder private constructor() : AbstractCashOrder(), StopOrder<Currency, Quote> {

    private val stopOrderSupport = StopLimitOrderSupport(this, ::stopPrice, ::dailyExecutionType)

    override val orderType: OrderType = OrderType.STOP_ORDER
    override lateinit var stopPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType

    override fun validateCurrentState() {
        super.validateCurrentState()
        stopOrderSupport.validateCurrentState()

        check(stopPrice.currency == priceCurrency) {
            "Stop price currency (${stopPrice.currency.safe}) differs from price currency (${priceCurrency.safe})." }
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)
        stopOrderSupport.validateNextState(nextState)
    }

    override fun toExecute(quote: Quote): Boolean = stopOrderSupport.toExecute(quote)

    companion object {
        fun create(
            base: Base,
            stopPrice: Amount,
            dailyExecutionType: DailyExecutionType,
        ): CashStopOrder {
            val order = CashStopOrder()
            base.copyToOrder(order)

            order.stopPrice   = stopPrice
            order.dailyExecutionType = dailyExecutionType

            order.validateCurrentState()
            return order
        }
    }
}


class CashMarketOrder private constructor() : AbstractCashOrder() {
    override val orderType: OrderType = OrderType.MARKET_ORDER

    override fun toExecute(quote: Quote): Boolean {
        val rateCurrencyPair = CurrencyPair(Currency(quote.product), quote.bid.currency)
        val orderCurrencyPair = CurrencyPair(buyCurrency, sellCurrency)

        check(rateCurrencyPair == orderCurrencyPair || rateCurrencyPair == orderCurrencyPair.inverted) {
            "FX rate currencies $rateCurrencyPair does not suite order currencies $orderCurrencyPair." }
        return true
    }

    companion object {
        fun create(
            base: Base,
        ): CashMarketOrder {
            val order = CashMarketOrder()
            base.copyToOrder(order)
            order.validateCurrentState()
            return order
        }
    }
}
