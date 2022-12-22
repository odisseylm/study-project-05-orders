package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
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
}
