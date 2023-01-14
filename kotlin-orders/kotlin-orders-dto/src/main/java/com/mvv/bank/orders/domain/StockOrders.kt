package com.mvv.bank.orders.domain

import com.mvv.bank.util.LateInitProperty
import java.math.BigDecimal
import java.time.ZonedDateTime


sealed class StockOrder : AbstractOrder<CompanySymbol, StockQuote>() {

    class Base (
        val id: Long? = null,
        val user: User,
        val side: Side,
        val buySellType: BuySellType,
        val company: Company,
        val volume: BigDecimal,

        val market: Market,

        val orderState: OrderState = OrderState.UNKNOWN,

        val placedAt:   ZonedDateTime?   = null,
        val executedAt: ZonedDateTime? = null,
        val canceledAt: ZonedDateTime? = null,
        val expiredAt:  ZonedDateTime?  = null,

        val resultingPrice: Amount? = null,
        val resultingQuote: StockQuote? = null,
    ) {
        fun copyToOrder(order: StockOrder) {
            order.id = id
            order.user = user

            order.side  = side
            order.buySellType = buySellType
            order.product = company.symbol
            order.company = company
            order.volume = volume

            order.market = market

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote
        }
    }

    private val companyImpl = LateInitProperty<Company, Any>(
        changeable = false,
        postUpdate = { new, _ -> product = new.symbol },
    )
    var company: Company by companyImpl

    override var resultingQuote: StockQuote?
        get() = super.resultingQuote
        set(value) {
            super.resultingQuote = value

            if (value != null && resultingPrice == null) {
                resultingPrice = value.asPrice(buySellType)
            }
        }
}

class StockLimitOrder : StockOrder(), LimitOrder<CompanySymbol, StockQuote> {

    private val limitOrderSupport = StopLimitOrderSupport(this,
        //"limitPrice", { this.limitPrice },
        //"dailyExecutionType", { this.dailyExecutionType },
        ::limitPrice,
        ::dailyExecutionType,
    )

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

    override fun toExecute(quote: StockQuote): Boolean = limitOrderSupport.toExecute(quote)

    companion object {
        fun create(
            base: Base,
            limitPrice: Amount,
            dailyExecutionType: DailyExecutionType,
        ): StockLimitOrder {
            val order = StockLimitOrder()
            base.copyToOrder(order)

            order.limitPrice = limitPrice
            order.dailyExecutionType = dailyExecutionType

            order.validateCurrentState()
            return order
        }
    }
}


class StockStopOrder : StockOrder(), StopOrder<CompanySymbol, StockQuote> {

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

    override fun toExecute(quote: StockQuote): Boolean = stopOrderSupport.toExecute(quote)

    companion object {
        fun create(
            base: Base,
            stopPrice: Amount,
            dailyExecutionType: DailyExecutionType,
        ): StockStopOrder {
            val order = StockStopOrder()
            base.copyToOrder(order)

            order.stopPrice = stopPrice
            order.dailyExecutionType = dailyExecutionType

            order.validateCurrentState()
            return order
        }
    }
}


class StockMarketOrder : StockOrder() {
    override val orderType: OrderType = OrderType.MARKET_ORDER

    override fun toExecute(quote: StockQuote): Boolean {
        check(quote.product == this.product.value) {
            "This quote is for another product (order: $product, quote: ${quote.product})." }
        return true
    }

    companion object {
        fun create(
            base: Base,
        ): StockMarketOrder {
            val order = StockMarketOrder()
            base.copyToOrder(order)

            order.validateCurrentState()
            return order
        }
    }
}
