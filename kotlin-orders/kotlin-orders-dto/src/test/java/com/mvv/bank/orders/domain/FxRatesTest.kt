package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

private fun bd(v: String) = BigDecimal(v)

class FxRatesTest {

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
        assertThat(invertRate(BigDecimal(480000))).isEqualByComparingTo(bd("0.0000020833"))
        assertThat(invertRate(BigDecimal.valueOf(480000.00))).isEqualByComparingTo(bd("0.0000020833"))
    }

    @Test
    fun inverted() {
        val marketDate = LocalDate.now()
        val marketDateTime = LocalDateTime.now()
        val marketZone = ZoneId.systemDefault()
        val dateTime = ZonedDateTime.of(marketDateTime, marketZone)

        val rate = FxRate("symbol", marketDate, marketDateTime, dateTime,
            CurrencyPair.of("AAA", "ZZZ"), bid = bd("10"), ask = bd("100"))
        val inverted = rate.inverted()

        assertThat(inverted)
            .isEqualTo(FxRate("symbol", marketDate, marketDateTime, dateTime,
            CurrencyPair.of("ZZZ", "AAA"), bid = bd("0.1"), ask = bd("0.01")))
    }
}
