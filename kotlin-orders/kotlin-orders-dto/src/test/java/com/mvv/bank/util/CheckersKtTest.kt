package com.mvv.bank.util

import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class CheckersKtTest {

    @Test
    fun checkNotNullAlways() {
        assertThatCode { checkNotNullAlways(123)}
            .doesNotThrowAnyException()
        assertThatCode { checkNotNullAlways("124") { "ExecutionType is not set." } }
            .doesNotThrowAnyException()

        assertThatCode { checkNotNullAlways(null) }
            .hasMessage("Required value was null.")
            .isExactlyInstanceOf(IllegalStateException::class.java)

        assertThatCode { checkNotNullAlways(null) { "ExecutionType is not set." } }
            .hasMessage("ExecutionType is not set.")
            .isExactlyInstanceOf(IllegalStateException::class.java)
    }
}