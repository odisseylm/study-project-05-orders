package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal


private fun bd(value: String) = BigDecimal(value)

class FxRateTest {

    @Test
    fun getMid() {
        val rate = FxRate(CurrencyPair.of("EUR_USD"), bd("1.05"), bd("1.07"))
        assertThat(rate.mid).isEqualTo(bd("1.06"))
    }

    @Test
    fun getSpread() {
        val rate = FxRate(CurrencyPair.of("EUR_USD"), bd("1.05"), bd("1.07"))
        assertThat(rate.spread).isEqualTo(bd("0.02"))
    }
}
