package com.mvv.bank.orders.domain

import com.mvv.bank.orders.service.MarketService
import java.time.*


class TestPredefinedMarkets {
    companion object : MarketFactory, MarketService {
        val NASDAQ: Market = TestMarketImpl("National Association of Securities Dealers Automated Quotation", "NASDAQ", ZoneId.of("America/New_York"), "", LocalTime.of(9, 0), LocalTime.of(17, 0))
        val KYIV1:  Market = TestMarketImpl("KYIV1", "KYIV1", ZoneId.of("Europe/Kiev"), "", LocalTime.of(9, 0), LocalTime.of(17, 0))

        override fun marketBySymbol(marketSymbol: String): Market =
            when (marketSymbol) {
                NASDAQ.symbol -> NASDAQ
                KYIV1.symbol  -> KYIV1
                else -> throw IllegalArgumentException("Market [$marketSymbol] is not found.")
            }

        override fun market(marketSymbol: String): Market = marketBySymbol(marketSymbol)
    }
}


class TestPredefinedCompanies {
    companion object : CompanyFactory {
        val APPLE: Company = TestCompanyImpl("Apple", "AAPL", "isin1234")

        override fun getCompanyData(symbol: String): Company =
            when (symbol) {
                APPLE.symbol -> APPLE
                else -> throw IllegalArgumentException("Company [$symbol] is not found.")
            }
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


class TestDateTimeService (
    override val zoneId: ZoneId = ZoneId.systemDefault(),
    override val clock: Clock = Clock.systemDefaultZone(),
    private val nowSupplier: ()->Instant = { Instant.now(clock) }

) : DateTimeService {

    override fun now(): Instant = nowSupplier()
}
