package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd


class AmountTest {

    @Test
    fun create() {
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
            val amount = Amount.of("12.34", Currency.USD)
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

    }
}