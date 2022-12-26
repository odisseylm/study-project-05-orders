package com.mvv.bank.orders.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId


class TestPredefinedMarkets {
    companion object {
        //val NASDAQ: Market = TestMarketImpl("National Association of Securities Dealers Automated Quotation", "NASDAQ", ZoneId.of("America/New_York"), "", LocalTime.of(9, 0), LocalTime.of(17, 0))
        val KYIV1: Market = TestMarketImpl("KYIV1", "KYIV1", ZoneId.of("Europe/Kiev"), "", LocalTime.of(9, 0), LocalTime.of(17, 0))
    }
}

class TestPredefinedCompanies {
    companion object {
        val APPLE: Company = TestCompanyImpl("Apple", "AAPL", "isin1234")
    }
}


private class TestCompanyImpl (
    override val name: String,
    override val symbol: String,
    override val isin: String,
) : Company

private class TestMarketImpl (
    override val name: String,
    override val symbol: String,
    override val zoneId: ZoneId,
    override val description: String,
    override val defaultOpenTime: LocalTime,  // inclusive
    override val defaultCloseTime: LocalTime, // exclusive
) : Market {
    override fun isWorkingDay(date: LocalDate): Boolean =
        (date.dayOfWeek == DayOfWeek.SATURDAY) || (date.dayOfWeek == DayOfWeek.SUNDAY)
}
