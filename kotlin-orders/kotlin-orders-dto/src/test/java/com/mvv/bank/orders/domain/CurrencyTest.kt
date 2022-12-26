package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test


internal class CurrencyTest {

    @Test
    fun createCurrency() {
        assertThat(Currency.of("USD").toString()).isEqualTo("USD")
        assertThat(Currency.of("USD").toString()).isEqualTo("USD")
        assertThat(Currency.valueOf("USD").toString()).isEqualTo("USD")

        assertThatCode { Currency.valueOf(" USD") }.hasMessage("Invalid currency [ USD].")
    }

    @Test
    fun createCurrencyPair() {
        val currencyPair = CurrencyPair.USD_EUR
        assertThat(currencyPair.toString()).isEqualTo("USD_EUR")
        assertThat(currencyPair.base).isEqualTo(Currency.of("USD"))
        assertThat(currencyPair.counter).isEqualTo(Currency.of("EUR"))

        assertThat(CurrencyPair.valueOf("USD_EUR")).isEqualTo(currencyPair)
        assertThat(CurrencyPair.of("USD_EUR")).isEqualTo(currencyPair)

        assertThatCode { CurrencyPair.valueOf("USD EUR") }
            .hasMessage("Invalid currency pair [USD EUR] (currency separator '_' is expected).")
        assertThatCode { CurrencyPair.valueOf("USD-EUR") }
            .hasMessage("Invalid currency pair [USD-EUR] (currency separator '_' is expected).")
        assertThatCode { CurrencyPair.valueOf("USD/EUR") }
            .hasMessage("Invalid currency pair [USD/EUR] (currency separator '_' is expected).")

        assertThatCode { CurrencyPair.valueOf("US1_EUR") }
            .hasMessage("Invalid currency pair [US1_EUR].")

        val currencyPair2 = CurrencyPair.of("USD", "UAH")
        assertThat(currencyPair2.toString()).isEqualTo("USD_UAH")
        assertThat(currencyPair2.base).isEqualTo(Currency.of("USD"))
        assertThat(currencyPair2.counter).isEqualTo(Currency.of("UAH"))
        assertThat(currencyPair2.base).isEqualTo(Currency.USD)
        assertThat(currencyPair2.counter).isEqualTo(Currency.UAH)
    }

    @Test
    fun copyCurrencyPair() {
        assertThat(CurrencyPair.USD_EUR.copy(counter = Currency.UAH))
            .isEqualTo(CurrencyPair.of("USD_UAH"))
        assertThat(CurrencyPair.USD_EUR.copy(counter = Currency.UAH))
            .isEqualTo(CurrencyPair.USD_UAH)
        assertThat(CurrencyPair.USD_EUR.copy(counter = Currency.UAH))
            .isEqualTo(CurrencyPair.of(Currency.USD, Currency.UAH))
    }
}
