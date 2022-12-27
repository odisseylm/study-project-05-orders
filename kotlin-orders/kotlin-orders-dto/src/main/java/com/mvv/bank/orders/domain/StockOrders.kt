package com.mvv.bank.orders.domain

import java.math.BigDecimal
import java.time.Instant


class StockLimitOrder : AbstractOrder<String, StockQuote>(), LimitOrder<String, StockQuote> {

    private val limitOrderSupport = LimitOrderSupport<String, StockQuote>()

    override val orderType: OrderType = OrderType.LIMIT_ORDER
    override lateinit var limitPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType
    lateinit var company: Company

    override fun validateCurrentState() {
        super.validateCurrentState()
        limitOrderSupport.validateCurrentState(this)
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)
        limitOrderSupport.validateNextState(this, nextState)
    }

    override fun toExecute(quote: StockQuote): Boolean = limitOrderSupport.toExecute(this, quote)

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
            // TODO: how to fix this duplicated 19 lines???
            order.id = id

            order.side  = side
            order.buySellType = buySellType
            order.product = companySymbol
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

            order.validateCurrentState()

            return order
        }
    }
}
