package com.mvv.bank.orders.domain.test

import com.mvv.bank.orders.domain.DateTimeService
import java.time.*


class TestDateTimeService (
    override val zoneId: ZoneId = ZoneId.systemDefault(),
    override val clock: Clock = Clock.systemDefaultZone(),
    private val nowSupplier: ()-> Instant = { Instant.now(clock) }

) : DateTimeService {

    override fun now(): Instant = nowSupplier()
}
