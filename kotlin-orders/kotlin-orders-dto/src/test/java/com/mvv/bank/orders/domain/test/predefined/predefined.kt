package com.mvv.bank.orders.domain.test.predefined

import com.mvv.bank.orders.domain.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId


class TestPredefinedUsers {
    companion object {
        val USER1 = User.of("vovan@gmail.com") //, "vovan")
        //val USER2 = User.of("petro@gmail.com") //, "petro")
    }
}


class TestPredefinedMarkets {
    companion object : MarketProvider {
        val NASDAQ: Market = TestMarketImpl("National Association of Securities Dealers Automated Quotation",
            MarketSymbol("NASDAQ"), ZoneId.of("America/New_York"), "",
            LocalTime.of(9, 0), LocalTime.of(17, 0))
        val KYIV1:  Market = TestMarketImpl("KYIV1", MarketSymbol("KYIV1"),
            ZoneId.of("Europe/Kiev"), "", LocalTime.of(9, 0), LocalTime.of(17, 0))

        override fun marketBySymbol(marketSymbol: MarketSymbol): Market =
            when (marketSymbol) {
                NASDAQ.symbol -> NASDAQ
                KYIV1.symbol  -> KYIV1
                else -> throw IllegalArgumentException("Market [$marketSymbol] is not found.")
            }
    }
}


class TestPredefinedCompanies {
    companion object : CompanyProvider {
        val APPLE: Company = TestCompanyImpl("Apple", CompanySymbol("AAPL"), "isin1234")

        override fun companyBySymbol(companySymbol: CompanySymbol): Company =
            when (companySymbol) {
                APPLE.symbol -> APPLE
                else -> throw IllegalArgumentException("Company [$companySymbol] is not found.")
            }
    }
}


private class TestCompanyImpl (
    override val name: String,
    override val symbol: CompanySymbol,
    override val isin: String,
) : Company


private class TestMarketImpl (
    override val name: String,
    override val symbol: MarketSymbol,
    override val zoneId: ZoneId,
    override val description: String,
    override val defaultOpenTime: LocalTime,  // inclusive
    override val defaultCloseTime: LocalTime, // exclusive
) : Market {
    override fun isWorkingDay(date: LocalDate): Boolean =
        (date.dayOfWeek == DayOfWeek.SATURDAY) || (date.dayOfWeek == DayOfWeek.SUNDAY)
}
