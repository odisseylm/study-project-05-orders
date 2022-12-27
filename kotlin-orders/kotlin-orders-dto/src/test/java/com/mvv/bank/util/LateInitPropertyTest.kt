package com.mvv.bank.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class LateInitPropertyTest {

    @Test
    fun asNullableValue() {

        val prop1 = LateInitProperty<Int, Any>(value = 125)
        assertThat(prop1.asNullableValue).isEqualTo(125)

        val prop2 = LateInitProperty<Int, Any>(value = null)
        assertThat(prop2.asNullableValue).isNull()
    }

    @Test
    fun unchangeable() {

        val prop1 = LateInitProperty<Int, Any>(value = null, changeable = false)
        prop1.set(1)
        prop1.set(1)
        assertThatCode { prop1.set(2) }
            .hasMessage("Not allowed to change property (from [1] to [2])")
            .isExactlyInstanceOf(IllegalStateException::class.java)
    }
}