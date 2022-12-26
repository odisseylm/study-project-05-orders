package com.mvv.bank.orders.domain

import java.time.Instant


class FxCashLimitOrder private constructor() : AbstractFxCashOrder(), LimitOrder<Currency, Quote> {

    private val limitOrderSupport = LimitOrderSupport<Currency, Quote>()

    override val orderType: OrderType = OrderType.LIMIT_ORDER
    override var limitPrice: Amount? = null
    override var dailyExecutionType: DailyExecutionType? = null

    override fun validateCurrentState() {
        super.validateCurrentState()
        limitOrderSupport.validateCurrentState(this)
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)
        limitOrderSupport.validateNextState(this, nextState)
    }

    override fun toExecute(quote: Quote): Boolean = limitOrderSupport.toExecute(this, quote)

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
            order.resultingRate = resultingRate

            order.validateCurrentState()

            return order
        }
    }
}
