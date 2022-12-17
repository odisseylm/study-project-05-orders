package com.mvv.bank.orders.domain

import java.time.Instant


class MarketOrder (
    id: Long?,
    currencyPair: CurrencyPair,
    @Suppress("MemberVisibilityCanBePrivate")
    val buySellType: BuySellType,
    resultingPrice: FxRate? = null,
    orderState: OrderState = OrderState.UNKNOWN,
    placedAt: Instant?,
    market: Market?,
) : BaseOrder(id, currencyPair, orderState, placedAt, market, resultingPrice) {

    override val orderType: OrderType = OrderType.LIMIT_ORDER

    fun toExecute(currentPrice: FxRate): Boolean {
        check(currencyPair == currentPrice.currencyPair)
        return true
    }
}
