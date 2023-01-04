package com.mvv.bank.orders.rest

import java.math.BigDecimal
import java.time.ZonedDateTime


class FxOrder {
    var id: Long? = null

    lateinit var user: String

    lateinit var orderType: OrderType
    lateinit var orderState: OrderState

    lateinit var market: String

    lateinit var side: Side
    lateinit var buySellType: BuySellType
    lateinit var buyCurrency: String
    lateinit var sellCurrency: String
    lateinit var volume: BigDecimal

    var limitPrice: BigDecimal? = null
    var stopPrice: BigDecimal? = null
    var dailyExecutionType: DailyExecutionType? = null

    var resultingRate: FxRate? = null

    var placedAt:   ZonedDateTime? = null
    var executedAt: ZonedDateTime? = null
    var canceledAt: ZonedDateTime? = null
    var expiredAt:  ZonedDateTime? = null
}
