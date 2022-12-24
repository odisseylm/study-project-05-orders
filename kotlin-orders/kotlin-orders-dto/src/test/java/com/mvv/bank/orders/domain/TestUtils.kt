package com.mvv.bank.orders.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId


enum class TestPredefinedMarkets (
    override val marketName: String,
    override val marketZoneId: ZoneId,
    override val description: String,
    override val defaultOpenTime: LocalTime,  // inclusive
    override val defaultCloseTime: LocalTime, // exclusive
) : Market {
    //NASDAQ("NASDAQ", ZoneId.of("America/New_York"), "", LocalTime.of(9, 0), LocalTime.of(17, 0)),
    KYIV_01("NASDAQ", ZoneId.of("Europe/Kiev"), "", LocalTime.of(9, 0), LocalTime.of(17, 0)),
    ;

    override fun isWorkingDay(date: LocalDate): Boolean =
        (date.dayOfWeek == DayOfWeek.SATURDAY) || (date.dayOfWeek == DayOfWeek.SUNDAY)
}

