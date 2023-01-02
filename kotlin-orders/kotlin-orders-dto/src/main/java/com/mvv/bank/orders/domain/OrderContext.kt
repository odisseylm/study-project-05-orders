package com.mvv.bank.orders.domain

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


interface DateTimeService {
    val clock: Clock
    val zoneId: ZoneId
    fun now(): Instant
}


interface GeneralContext {
    val dateTimeService: DateTimeService
}


interface OrderContext : GeneralContext {
    val market: Market

    /** Returns current data-time as ZonedDateTime with some (unspecified, can be system/server or client) time-zone. */
    fun now(): ZonedDateTime = dateTimeService.now().atZone(dateTimeService.zoneId)

    /** Returns current data-time as ZonedDateTime with market time-zone. */
    fun nowOnMarket(): ZonedDateTime = now().withZoneSameInstant(market.zoneId)

    companion object {
        fun create(
            dateTimeService: DateTimeService,
            market: Market,
        ) : OrderContext = OrderContextImpl(
            dateTimeService = dateTimeService,
            market = market,
        )
    }
}


private class OrderContextImpl (
    override val dateTimeService: DateTimeService,
    override val market: Market,
) : OrderContext
