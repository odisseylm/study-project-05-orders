package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LimitUtilsTests {

    @Test
    fun justUseDailyExecutionType() {
        assertThat(DailyExecutionType.DAY_ONLY.humanName).isEqualTo("Day Only")
        assertThat(DailyExecutionType.GTC.humanName).isEqualTo("Good 'til Canceled")
    }
}
