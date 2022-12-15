package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test


internal class CurrencyTest {

    @Test
    fun createCurrency() {
        assertThat(Currency("USD").toString()).isEqualTo("USD")
        assertThat(Currency.of("USD").toString()).isEqualTo("USD")
        assertThat(Currency.valueOf("USD").toString()).isEqualTo("USD")

        assertThatCode { Currency.valueOf(" USD") }.hasMessage("Invalid currency [ USD].")
    }

    @Test
    fun createCurrencyPair() {
        val currencyPair = CurrencyPair(Currency("USD"), Currency("EUR"))
        assertThat(currencyPair.toString()).isEqualTo("USD_EUR")
        assertThat(currencyPair.base).isEqualTo(Currency("USD"))
        assertThat(currencyPair.counter).isEqualTo(Currency("EUR"))

        assertThat(CurrencyPair.valueOf("USD_EUR")).isEqualTo(currencyPair)
        assertThat(CurrencyPair.of("USD_EUR")).isEqualTo(currencyPair)

        assertThatCode { CurrencyPair.valueOf("USD EUR") }.hasMessage("Invalid currency pair [USD EUR].")
        assertThatCode { CurrencyPair.valueOf("USD-EUR") }.hasMessage("Invalid currency pair [USD-EUR].")
        assertThatCode { CurrencyPair.valueOf("USD/EUR") }.hasMessage("Invalid currency pair [USD/EUR].")

        assertThatCode { CurrencyPair.valueOf("US1_EUR") }.hasMessage("Invalid currency pair [US1_EUR].")
    }
}
