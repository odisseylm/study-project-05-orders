package com.mvv.bank.orders.domain

import java.math.BigDecimal
import java.time.Instant


class StockLimitOrder : AbstractOrder<String, StockQuote>(), LimitOrder<String, StockQuote> {

    private val limitOrderSupport = StopLimitOrderSupport(this,
        //"limitPrice", { this.limitPrice },
        //"dailyExecutionType", { this.dailyExecutionType },
        ::limitPrice,
        ::dailyExecutionType,
    )

    override val orderType: OrderType = OrderType.LIMIT_ORDER
    override lateinit var limitPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType
    lateinit var company: Company

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
            side: Side,
            buySellType: BuySellType,
            companySymbol: String,
            company: Company,
            volume: BigDecimal,
            limitPrice: Amount,
            dailyExecutionType: DailyExecutionType,

            marketSymbol: String,
            market: Market,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt: Instant?   = null,
            executedAt: Instant? = null,
            canceledAt: Instant? = null,
            expiredAt: Instant?  = null,

            resultingPrice: Amount? = null,
            resultingQuote: StockQuote? = null,
        ): StockLimitOrder {
            val order = StockLimitOrder()
            // TODO: how to fix this duplicated 19 lines???
            order.id = id

            order.side  = side
            order.buySellType = buySellType
            order.product = companySymbol
            order.company = company
            order.volume = volume
            order.limitPrice = limitPrice
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

            order.validateCurrentState()

            return order
        }
    }
}


class StockStopOrder : AbstractOrder<String, StockQuote>(), StopOrder<String, StockQuote> {

    private val stopOrderSupport = StopLimitOrderSupport(this, ::stopPrice, ::dailyExecutionType)

    override val orderType: OrderType = OrderType.STOP_ORDER
    override lateinit var stopPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType
    lateinit var company: Company

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
            side: Side,
            buySellType: BuySellType,
            companySymbol: String,
            company: Company,
            volume: BigDecimal,
            stopPrice: Amount,
            dailyExecutionType: DailyExecutionType,

            marketSymbol: String,
            market: Market,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt: Instant?   = null,
            executedAt: Instant? = null,
            canceledAt: Instant? = null,
            expiredAt: Instant?  = null,

            resultingPrice: Amount? = null,
            resultingQuote: StockQuote? = null,
        ): StockStopOrder {
            val order = StockStopOrder()
            // T O D O: how to fix this duplicated 19 lines???
            order.id = id

            order.side  = side
            order.buySellType = buySellType
            order.product = companySymbol
            order.company = company
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

            order.validateCurrentState()

            return order
        }
    }
}


class StockMarketOrder : AbstractOrder<String, StockQuote>() {

    override val orderType: OrderType = OrderType.MARKET_ORDER
    var company: Company? = null

    override fun toExecute(quote: StockQuote): Boolean {
        check(quote.productSymbol == this.product) {
            "This quote is for another product (order: $product, quote: ${quote.productSymbol})." }
        return true
    }

    companion object {
        fun create(
            id: Long? = null,
            side: Side,
            buySellType: BuySellType,
            companySymbol: String,
            company: Company,
            volume: BigDecimal,

            marketSymbol: String,
            market: Market,

            orderState: OrderState = OrderState.UNKNOWN,

            placedAt: Instant?   = null,
            executedAt: Instant? = null,
            canceledAt: Instant? = null,
            expiredAt: Instant?  = null,

            resultingPrice: Amount? = null,
            resultingQuote: StockQuote? = null,
        ): StockMarketOrder {
            val order = StockMarketOrder()
            // T O D O: how to fix this duplicated 19 lines???
            order.id = id

            order.side  = side
            order.buySellType = buySellType
            order.product = companySymbol
            order.company = company
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

            order.validateCurrentState()

            return order
        }
    }
}