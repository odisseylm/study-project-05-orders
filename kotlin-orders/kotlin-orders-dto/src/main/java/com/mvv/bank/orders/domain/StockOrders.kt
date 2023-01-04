package com.mvv.bank.orders.domain

import com.mvv.bank.util.LateInitProperty
import java.math.BigDecimal
import java.time.ZonedDateTime


sealed class StockOrder : AbstractOrder<CompanySymbol, StockQuote>() {
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
            id: Long? = null,
            user: User,
            side: Side,
            buySellType: BuySellType,
            company: Company,
            volume: BigDecimal,
            limitPrice: Amount,
            dailyExecutionType: DailyExecutionType,

            market: Market,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt:   ZonedDateTime?   = null,
            executedAt: ZonedDateTime? = null,
            canceledAt: ZonedDateTime? = null,
            expiredAt:  ZonedDateTime?  = null,

            resultingPrice: Amount? = null,
            resultingQuote: StockQuote? = null,
        ): StockLimitOrder {
            val order = StockLimitOrder()
            // TODO: how to fix this duplicated 19 lines???
            order.id = id
            order.user = user

            order.side  = side
            order.buySellType = buySellType
            order.product = company.symbol
            order.company = company
            order.volume = volume
            order.limitPrice = limitPrice
            order.dailyExecutionType = dailyExecutionType

            order.market = market

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote

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
            id: Long? = null,
            user: User,
            market: Market,
            side: Side,

            buySellType: BuySellType,
            company: Company,
            volume: BigDecimal,
            stopPrice: Amount,
            dailyExecutionType: DailyExecutionType,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt:   ZonedDateTime?   = null,
            executedAt: ZonedDateTime? = null,
            canceledAt: ZonedDateTime? = null,
            expiredAt:  ZonedDateTime?  = null,

            resultingPrice: Amount? = null,
            resultingQuote: StockQuote? = null,
        ): StockStopOrder {
            val order = StockStopOrder()
            // T O D O: how to fix this duplicated 19 lines???
            order.id = id
            order.user = user
            order.market = market

            order.side  = side
            order.buySellType = buySellType
            order.product = company.symbol
            order.company = company
            order.volume = volume
            order.stopPrice = stopPrice
            order.dailyExecutionType = dailyExecutionType

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote

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
            id: Long? = null,
            user: User,
            side: Side,
            market: Market,

            buySellType: BuySellType,
            company: Company,
            volume: BigDecimal,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt:   ZonedDateTime?   = null,
            executedAt: ZonedDateTime? = null,
            canceledAt: ZonedDateTime? = null,
            expiredAt:  ZonedDateTime?  = null,

            resultingPrice: Amount? = null,
            resultingQuote: StockQuote? = null,
        ): StockMarketOrder {
            val order = StockMarketOrder()
            // T O D O: how to fix this duplicated 19 lines???
            order.id = id
            order.user = user
            order.market = market
            order.side  = side

            order.buySellType = buySellType
            order.product = company.symbol
            order.company = company
            order.volume = volume

            order.orderState = orderState

            order.placedAt   = placedAt
            order.executedAt = executedAt
            order.canceledAt = canceledAt
            order.expiredAt  = expiredAt

            order.resultingPrice = resultingPrice
            order.resultingQuote = resultingQuote

            order.validateCurrentState()

            return order
        }
    }
}
