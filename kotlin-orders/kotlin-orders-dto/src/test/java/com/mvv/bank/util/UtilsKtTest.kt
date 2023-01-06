package com.mvv.bank.util

import com.mvv.bank.orders.domain.CashLimitOrder
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


internal class UtilsTest {

    @Test
    @DisplayName("newInstance")
    fun testNewInstance() {
        Assertions.assertThat(newInstance<CashLimitOrder>()).isExactlyInstanceOf(CashLimitOrder::class.java)
    }

    @Test
    @DisplayName("internalNewInstance")
    fun testInternalNewInstance() {
        Assertions.assertThat(internalNewInstance(CashLimitOrder::class)).isExactlyInstanceOf(CashLimitOrder::class.java)
    }

    @Test
    @DisplayName("newJavaInstance")
    fun testNewJavaInstance() {
        Assertions.assertThat(newJavaInstance(CashLimitOrder::class.java)).isExactlyInstanceOf(CashLimitOrder::class.java)
    }
}
