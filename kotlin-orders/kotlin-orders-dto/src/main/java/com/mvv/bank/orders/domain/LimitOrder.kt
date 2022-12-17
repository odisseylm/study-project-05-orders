package com.mvv.bank.orders.domain

import java.time.Instant

// T O D O: find better name
enum class ExecutionType (val humanName: String) {
    DAILY_ONLY("Day Only"),
    GTC("Good 'til Canceled"),
}

class LimitOrder (
    id: Long?,
    currencyPair: CurrencyPair,
    @Suppress("MemberVisibilityCanBePrivate")
    val buySellType: BuySellType,
    @Suppress("MemberVisibilityCanBePrivate")
    val executionType: ExecutionType,
    @Suppress("MemberVisibilityCanBePrivate")
    val price: FxRate,
    resultingPrice: FxRate? = null,
    orderState: OrderState = OrderState.UNKNOWN,
    placedAt: Instant?,
    market: Market?,
) : BaseOrder(id, currencyPair, orderState, placedAt, market, resultingPrice) {

    override val orderType: OrderType = OrderType.LIMIT_ORDER

    init {
        check(price.currencyPair == currencyPair) {
            "Invalid price currency pair ${price.currencyPair} for $currencyPair order."
        }
    }

    fun toExecute(currentPrice: FxRate): Boolean {
        check(currencyPair == currentPrice.currencyPair)
        check(price.currencyPair == currentPrice.currencyPair)

        return when (buySellType) {
            BuySellType.BUY  -> currentPrice.bid <= this.price.bid
            BuySellType.SELL -> currentPrice.ask >= this.price.ask
        }
    }
}
