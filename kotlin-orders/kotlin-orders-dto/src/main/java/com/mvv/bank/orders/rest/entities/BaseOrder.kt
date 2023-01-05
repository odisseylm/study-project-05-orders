package com.mvv.bank.orders.rest.entities

import java.math.BigDecimal
import java.time.ZonedDateTime


sealed class BaseOrder {
    var id: Long? = null
    lateinit var user: String
    lateinit var market: String

    lateinit var orderType: OrderType
    lateinit var orderState: OrderState
    lateinit var side: Side
    lateinit var buySellType: BuySellType
    lateinit var volume: BigDecimal

    var limitPrice: Amount? = null
    var stopPrice:  Amount? = null
    var dailyExecutionType: DailyExecutionType? = null

    var placedAt:   ZonedDateTime? = null
    var executedAt: ZonedDateTime? = null
    var canceledAt: ZonedDateTime? = null
    var expiredAt:  ZonedDateTime? = null
}
