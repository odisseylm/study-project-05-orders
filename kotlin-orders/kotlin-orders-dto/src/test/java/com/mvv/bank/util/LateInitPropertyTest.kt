package com.mvv.bank.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test

internal class LateInitPropertyTest {

    @Test
    fun asNullableValue() {

        val prop1 = LateInitProperty<Int, Any>(value = 125)
        assertThat(prop1.asNullableValue).isEqualTo(125)

        val prop2 = LateInitProperty<Int, Any>(value = null)
        assertThat(prop2.asNullableValue).isNull()
    }

    @Test
    fun unchangeable() {

        SoftAssertions().apply {

            run {
                val prop = LateInitProperty<Int, Any>(value = null, changeable = false)
                prop.set(1)
                prop.set(1)

                assertThat(prop.asNullableValue).isEqualTo(1)    // to suppress 'unused' warning
                assertThat(prop.asNonNullableValue).isEqualTo(1) // to suppress 'unused' warning

                assertThatCode { prop.set(2) }
                    .hasMessage("Not allowed to change property (from [1] to [2]).")
                    .isExactlyInstanceOf(IllegalStateException::class.java)
            }

            run {
                val prop = LateInitProperty<Int, Any>(propName = "id", changeable = false)
                assertThat(prop.propName).isEqualTo("id") // only for coverage/usage
                prop.set(1)
                prop.set(1)
                assertThatCode { prop.set(2) }
                    .hasMessage("Not allowed to change property 'id' (from [1] to [2]).")
                    .isExactlyInstanceOf(IllegalStateException::class.java)
            }

            run {
                val prop = LateInitProperty<Int, Any>(
                    changeable = false,
                    changeErrorMessage = "Not allowed to change order ID (from [\${prev}] to [\${new}])."
                )
                prop.set(1)
                prop.set(1)
                assertThatCode { prop.set(2) }
                    .hasMessage("Not allowed to change order ID (from [1] to [2]).")
                    .isExactlyInstanceOf(IllegalStateException::class.java)
            }

        }.assertAll()
    }
}