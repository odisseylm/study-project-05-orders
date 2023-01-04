package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd
import java.time.*


class FxRateTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testMarketDate = LocalDate.of(2022, 12, 19)
    private val testMarketTime = LocalTime.of(1, 2, 3)
    private val marketZonedDateTime = ZonedDateTime.of(testMarketDate, testMarketTime, testMarket.zoneId)

    @Test
    fun getMid() {
        val rate = FxRate.of(
            testMarket, marketZonedDateTime,
            CurrencyPair.of("EUR_USD"), bd("1.05"), bd("1.07"))
        assertThat(rate.mid).isEqualTo(bd("1.06"))
    }

    @Test
    fun getSpread() {
        val rate = FxRate(
            testMarket.symbol, marketZonedDateTime, testMarketDate, testMarketTime,
            CurrencyPair.of("EUR_USD"), bd("1.05"), bd("1.07"))
        assertThat(rate.spread).isEqualTo(bd("0.02"))
    }

    @Test
    @DisplayName("invertRate")
    fun test_invertRate() {
        SoftAssertions().apply {
            //assertThat(invertRate(0))
            //assertThat(invertRate(0.0))
            //assertThat(invertRate(0.00))

            //assertThat(invertRate(bd("1"))).isEqualByComparingTo(bd("1"))
            //assertThat(invertRate(bd("1.0"))).isEqualByComparingTo(bd("1"))
            //assertThat(invertRate(bd("1.00"))).isEqualByComparingTo(bd("1"))

            //assertThat(invertRate(bd("1.06"))).isEqualByComparingTo(bd("0.94340"))
            //assertThat(invertRate(bd("1.06"))).isEqualByComparingTo(bd("0.94340"))

            assertThat(invertRate(bd("136.13"))).isEqualByComparingTo(bd("0.0073459"))

            // USD_JPY
            assertThat(invertRate(bd("132.77"))).isEqualByComparingTo(bd("0.0075318"))
            assertThat(invertRate(bd("130.00"))).isEqualByComparingTo(bd("0.0076923"))
            // XAU/USD 1798.18
            assertThat(invertRate(bd("1798.18"))).isEqualByComparingTo(bd("0.00055612"))
            assertThat(invertRate(bd("1800.00"))).isEqualByComparingTo(bd("0.00055556"))

            assertThat(invertRate(bd("484149.00"))).isEqualByComparingTo(bd("0.0000020655"))
            assertThat(invertRate(bd("484149.0"))).isEqualByComparingTo(bd("0.0000020655"))
            assertThat(invertRate(bd("484149"))).isEqualByComparingTo(bd("0.0000020655"))
            assertThat(invertRate(bd("480000.00"))).isEqualByComparingTo(bd("0.0000020833"))
            assertThat(invertRate(bd("480000"))).isEqualByComparingTo(bd("0.0000020833"))
            assertThat(invertRate(bd(480000))).isEqualByComparingTo(bd("0.0000020833"))
            assertThat(invertRate(bd.valueOf(480000.00))).isEqualByComparingTo(bd("0.0000020833"))

        }.assertAll()
    }

    @Test
    fun inverted() {
        val marketDate = LocalDate.now()
        val marketTime = LocalTime.now()
        val marketDateTime = LocalDateTime.of(marketDate, marketTime)
        val marketZone = ZoneId.systemDefault()
        val zonedDateTime = ZonedDateTime.of(marketDateTime, marketZone)

        val rate = FxRate(testMarket.symbol, zonedDateTime, marketDate, marketTime,
            CurrencyPair.of("AAA", "ZZZ"), bid = bd("10"), ask = bd("100")
        )
        val inverted = rate.inverted()

        assertThat(inverted)
            .isEqualTo(FxRate(testMarket.symbol, zonedDateTime, marketDate, marketTime,
                CurrencyPair.of("ZZZ", "AAA"), bid = bd("0.1"), ask = bd("0.01")
            ))
    }
}
