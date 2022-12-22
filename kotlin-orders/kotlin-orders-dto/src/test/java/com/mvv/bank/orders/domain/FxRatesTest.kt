package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import java.math.BigDecimal

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

        assertThat(invertRate(bd("136.13"))).isEqualByComparingTo(bd("0.0073"))
    }
}
