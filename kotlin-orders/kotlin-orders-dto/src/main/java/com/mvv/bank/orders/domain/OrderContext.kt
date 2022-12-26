package com.mvv.bank.orders.domain

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime


interface DateTimeService {
    val clock: Clock
    fun now(): Instant
}


interface GeneralContext {
    val dateTimeService: DateTimeService
}


interface OrderContext : GeneralContext {
    val market: Market
    fun now(): Instant = dateTimeService.now()
    fun nowOnMarket(): LocalDateTime = LocalDateTime.ofInstant(now(), market.zoneId)

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
