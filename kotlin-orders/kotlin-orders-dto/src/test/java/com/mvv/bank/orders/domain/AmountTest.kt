package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.Currency.Companion.EUR
import com.mvv.bank.orders.domain.Currency.Companion.USD
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd


internal class AmountTest {

    @Test
    fun create() {

        SoftAssertions().apply {

            run {
                val amount = Amount.of(bd("12.34"), Currency.of("USD"))
                assertThat(amount.value).isEqualTo(bd("12.34"))
                assertThat(amount.currency).isEqualTo(Currency.of("USD"))
                assertThat(amount.currency.value).isEqualTo("USD")
            }

            run {
                val amount = Amount(bd("12.34"), Currency.of("USD"))
                assertThat(amount.value).isEqualTo(bd("12.34"))
                assertThat(amount.currency).isEqualTo(Currency.of("USD"))
                assertThat(amount.currency.value).isEqualTo("USD")
            }

            run {
                val amount = Amount(bd("12.34"), USD)
                assertThat(amount.value).isEqualTo(bd("12.34"))
                assertThat(amount.currency).isEqualTo(Currency.of("USD"))
                assertThat(amount.currency.value).isEqualTo("USD")
            }

            run {
                val amount = Amount.valueOf("12.34 USD")
                assertThat(amount.value).isEqualTo(bd("12.34"))
                assertThat(amount.currency).isEqualTo(Currency.of("USD"))
                assertThat(amount.currency.value).isEqualTo("USD")
            }

            run {
                val amount = Amount.valueOf("12.34 JPY")
                assertThat(amount.value).isEqualTo(bd("12.34"))
                assertThat(amount.currency).isEqualTo(Currency.JPY)
                assertThat(amount.currency.value).isEqualTo("JPY")
            }

        }.assertAll()
    }

    @Test
    fun compare() {
        SoftAssertions().apply {

            assertThat(Amount(bd("1234.5"), USD)).isEqualTo(Amount(bd("1234.5"), USD))
            assertThat(Amount(bd("1234.5"), USD)).isNotEqualTo(Amount(bd("1234.5"), EUR))

            // with another precision
            assertThat(Amount(bd("1234.5"), USD)).isEqualTo(Amount(bd("1234.50"), USD))

        }.assertAll()
    }

    @Test
    @DisplayName("hashCode")
    fun testHashCode() {
        SoftAssertions().apply {

            println(bd("30000").scale())
            println(bd("30000").precision())
            println(bd("30000.00").scale())
            println(bd("30000.00").precision())
            println(bd("0.001").scale())
            println(bd("0.001").precision())
            println(bd("3e5").scale())
            println(bd("3e5").precision())

            assertThat(Amount(bd("0"), USD).hashCode()).isEqualTo(Amount(bd("0"), USD).hashCode())
            assertThat(Amount(bd("1234.5"), USD).hashCode()).isEqualTo(Amount(bd("1234.5"), USD).hashCode())
            assertThat(Amount(bd("1234.5"), USD).hashCode()).isNotEqualTo(Amount(bd("1234.5"), EUR).hashCode())

            // with another precision
            assertThat(Amount(bd("1234.5"), USD).hashCode()).isEqualTo(Amount(bd("1234.5000"), USD).hashCode())
            assertThat(Amount(bd("3e5"), USD).hashCode()).isEqualTo(Amount(bd("30e4"), USD).hashCode())

        }.assertAll()
    }
}