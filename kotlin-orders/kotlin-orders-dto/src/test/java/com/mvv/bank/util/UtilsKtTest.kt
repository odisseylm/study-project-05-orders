package com.mvv.bank.util

import com.mvv.bank.orders.domain.FxCashLimitOrder
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class UtilsTest {

    @Test
    @DisplayName("newInstance")
    fun testNewInstance() {
        Assertions.assertThat(newInstance<FxCashLimitOrder>()).isExactlyInstanceOf(FxCashLimitOrder::class.java)
    }

    @Test
    @DisplayName("internalNewInstance")
    fun testInternalNewInstance() {
        Assertions.assertThat(internalNewInstance(FxCashLimitOrder::class)).isExactlyInstanceOf(FxCashLimitOrder::class.java)
    }

    @Test
    @DisplayName("newJavaInstance")
    fun testNewJavaInstance() {
        Assertions.assertThat(newJavaInstance(FxCashLimitOrder::class.java)).isExactlyInstanceOf(FxCashLimitOrder::class.java)
    }
}
