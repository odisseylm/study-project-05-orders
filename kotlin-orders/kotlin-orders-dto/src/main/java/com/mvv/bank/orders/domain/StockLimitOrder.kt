package com.mvv.bank.orders.domain

import java.time.Instant


class StockLimitOrder : AbstractLimitOrder<String, StockQuote>() {

    var company: Company? = null

    companion object {
        fun create(
            id: Long? = null,
            side: Side,
            buySellType: BuySellType,
            companySymbol: String,
            company: Company,
            limitPrice: Amount,
            executionType: ExecutionType,

            market: Market,
            orderState: OrderState = OrderState.UNKNOWN,

            placedAt: Instant? = null,
            executedAt: Instant? = null,
            canceledAt: Instant? = null,
            expiredAt: Instant? = null,

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

            order.validateCurrentState()

            return order
        }
    }
}
