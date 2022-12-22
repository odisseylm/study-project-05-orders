package com.mvv.bank.orders.domain

/*
import java.time.Instant


class StopLossOrder (
    id: Long?,
    currencyPair: CurrencyPair,
    @Suppress("MemberVisibilityCanBePrivate")
    val buySellType: BuySellType,
    @Suppress("MemberVisibilityCanBePrivate")
    val stopPrice: FxRate,
    resultingPrice: FxRate? = null,
    orderState: OrderState = OrderState.UNKNOWN,
    placedAt: Instant?,
    market: Market?,
) : AbstractOrder(id, currencyPair, orderState, placedAt, market, resultingPrice) {

    override val orderType: OrderType = OrderType.LIMIT_ORDER

    init {
        check(stopPrice.currencyPair == currencyPair) {
            "Invalid price currency pair ${stopPrice.currencyPair} for $currencyPair order."
        }
    }

    fun toExecute(currentPrice: FxRate): Boolean {
        check(currencyPair == currentPrice.currencyPair)
        check(stopPrice.currencyPair == currentPrice.currencyPair)

        return when (buySellType) {
            BuySellType.BUY  -> currentPrice.bid <= this.stopPrice.bid
            BuySellType.SELL -> currentPrice.ask >= this.stopPrice.ask
        }
    }
}
*/
