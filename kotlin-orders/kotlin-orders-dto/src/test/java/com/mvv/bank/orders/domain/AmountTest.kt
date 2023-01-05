package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.Currency.Companion.EUR
import com.mvv.bank.orders.domain.Currency.Companion.USD
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd


class AmountTest {

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
                val amount = Amount.of("12.34", Currency.of("USD"))
                assertThat(amount.value).isEqualTo(bd("12.34"))
                assertThat(amount.currency).isEqualTo(Currency.of("USD"))
                assertThat(amount.currency.value).isEqualTo("USD")
            }

            run {
                val amount = Amount.of("12.34", USD)
                assertThat(amount.value).isEqualTo(bd("12.34"))
                assertThat(amount.currency).isEqualTo(Currency.of("USD"))
                assertThat(amount.currency.value).isEqualTo("USD")
            }

            run {
                val amount = Amount.of("12.34 USD")
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

            assertThat(Amount.of("1234.5", USD)).isEqualTo(Amount.of("1234.5", USD))
            assertThat(Amount.of("1234.5", USD)).isNotEqualTo(Amount.of("1234.5", EUR))

            assertThat(Amount.of("1234.5", USD)).isEqualTo(Amount.of("1234.50", USD))

        }.assertAll()
    }
}