package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


private fun bd(value: String) = BigDecimal(value)

class FxRateTest {
    private val market = "market1"
    private val marketDate = LocalDate.of(2022, 12, 19)
    private val marketDateTime = LocalDateTime.of(2022, 12, 19, 1, 2, 3)
    private val dateTime = ZonedDateTime.now() // T O D O: use hardcoded one

    @Test
    fun getMid() {
        val rate = FxRate(
            market, marketDate, marketDateTime, dateTime,
            CurrencyPair.of("EUR_USD"), bd("1.05"), bd("1.07"))
        assertThat(rate.mid).isEqualTo(bd("1.06"))
    }

    @Test
    fun getSpread() {
        val rate = FxRate(
            market, marketDate, marketDateTime, dateTime,
            CurrencyPair.of("EUR_USD"), bd("1.05"), bd("1.07"))
        assertThat(rate.spread).isEqualTo(bd("0.02"))
    }

    @Test
    @DisplayName("invertRate")
    fun test_invertRate() {
        //assertThat(invertRate(0))
        //assertThat(invertRate(0.0))
        //assertThat(invertRate(0.00))

        //assertThat(invertRate(bd("1"))).isEqualByComparingTo(bd("1"))
        //assertThat(invertRate(bd("1.0"))).isEqualByComparingTo(bd("1"))
        //assertThat(invertRate(bd("1.00"))).isEqualByComparingTo(bd("1"))

        //assertThat(invertRate(bd("1.06"))).isEqualByComparingTo(bd("0.94340"))
        //assertThat(invertRate(bd("1.06"))).isEqualByComparingTo(bd("0.94340"))

        assertThat(invertRate(BigDecimal("136.13"))).isEqualByComparingTo(BigDecimal("0.0073459"))

        // USD_JPY
        assertThat(invertRate(BigDecimal("132.77"))).isEqualByComparingTo(BigDecimal("0.0075318"))
        assertThat(invertRate(BigDecimal("130.00"))).isEqualByComparingTo(BigDecimal("0.0076923"))
        // XAU/USD 1798.18
        assertThat(invertRate(BigDecimal("1798.18"))).isEqualByComparingTo(BigDecimal("0.00055612"))
        assertThat(invertRate(BigDecimal("1800.00"))).isEqualByComparingTo(BigDecimal("0.00055556"))

        assertThat(invertRate(BigDecimal("484149.00"))).isEqualByComparingTo(BigDecimal("0.0000020655"))
        assertThat(invertRate(BigDecimal("484149.0"))).isEqualByComparingTo(BigDecimal("0.0000020655"))
        assertThat(invertRate(BigDecimal("484149"))).isEqualByComparingTo(BigDecimal("0.0000020655"))
        assertThat(invertRate(BigDecimal("480000.00"))).isEqualByComparingTo(BigDecimal("0.0000020833"))
        assertThat(invertRate(BigDecimal("480000"))).isEqualByComparingTo(BigDecimal("0.0000020833"))
        assertThat(invertRate(BigDecimal(480000))).isEqualByComparingTo(BigDecimal("0.0000020833"))
        assertThat(invertRate(BigDecimal.valueOf(480000.00))).isEqualByComparingTo(BigDecimal("0.0000020833"))
    }

    @Test
    fun inverted() {
        val marketDate = LocalDate.now()
        val marketDateTime = LocalDateTime.now()
        val marketZone = ZoneId.systemDefault()
        val dateTime = ZonedDateTime.of(marketDateTime, marketZone)

        val rate = FxRate("symbol", marketDate, marketDateTime, dateTime,
            CurrencyPair.of("AAA", "ZZZ"), bid = BigDecimal("10"), ask = BigDecimal("100")
        )
        val inverted = rate.inverted()

        assertThat(inverted)
            .isEqualTo(FxRate("symbol", marketDate, marketDateTime, dateTime,
                CurrencyPair.of("ZZZ", "AAA"), bid = BigDecimal("0.1"), ask = BigDecimal("0.01")
            ))
    }
}
