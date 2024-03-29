package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaType


internal class CurrencyTest {

    @Test
    fun createCurrency() {
        SoftAssertions().apply {

            assertThat(Currency.of("USD").toString()).isEqualTo("USD")
            assertThat(Currency.of("USD").toString()).isEqualTo("USD")
            assertThat(Currency.valueOf("USD").toString()).isEqualTo("USD")

            assertThatCode { Currency.valueOf(" USD") }.hasMessage("Invalid currency [ USD].")

        }.assertAll()
    }

    @Test
    fun createCurrencyPair() {
        SoftAssertions().apply {

            val currencyPair = CurrencyPair.USD_EUR
            assertThat(currencyPair.toString()).isEqualTo("USD_EUR")
            assertThat(currencyPair.base).isEqualTo(Currency.of("USD"))
            assertThat(currencyPair.counter).isEqualTo(Currency.of("EUR"))

            assertThat(CurrencyPair.valueOf("USD_EUR")).isEqualTo(currencyPair)
            assertThat(CurrencyPair.valueOf("USD_EUR")).isEqualTo(currencyPair)

            assertThatCode { CurrencyPair.valueOf("USD EUR") }
                .hasMessage("Invalid currency pair [USD EUR] (format like 'USD_EUR' is expected).")
            assertThatCode { CurrencyPair.valueOf("USD-EUR") }
                .hasMessage("Invalid currency pair [USD-EUR] (format like 'USD_EUR' is expected).")
            assertThatCode { CurrencyPair.valueOf("USD/EUR") }
                .hasMessage("Invalid currency pair [USD/EUR] (format like 'USD_EUR' is expected).")

            assertThatCode { CurrencyPair.valueOf("US1_EUR") }
                .hasMessage("Invalid currency pair [US1_EUR].")

            val currencyPair2 = CurrencyPair.of("USD", "UAH")
            assertThat(currencyPair2.toString()).isEqualTo("USD_UAH")
            assertThat(currencyPair2.base).isEqualTo(Currency.of("USD"))
            assertThat(currencyPair2.counter).isEqualTo(Currency.of("UAH"))
            assertThat(currencyPair2.base).isEqualTo(Currency.USD)
            assertThat(currencyPair2.counter).isEqualTo(Currency.UAH)

        }.assertAll()
    }

    @Test
    fun copyCurrencyPair() {
        SoftAssertions().apply {

            assertThat(CurrencyPair.USD_EUR.copy(counter = Currency.UAH))
                .isEqualTo(CurrencyPair.valueOf("USD_UAH"))
            assertThat(CurrencyPair.USD_EUR.copy(counter = Currency.UAH))
                .isEqualTo(CurrencyPair.USD_UAH)
            assertThat(CurrencyPair.USD_EUR.copy(counter = Currency.UAH))
                .isEqualTo(CurrencyPair.of(Currency.USD, Currency.UAH))

        }.assertAll()
    }

    @Test
    fun testPredefinedCurrencyPairs() {
        SoftAssertions().apply {

            /*
        val properPredefinedCurrencyPairs: Int = CurrencyPair.Companion::class.java.declaredFields
            .filter { it.canAccess(null) && it.type == CurrencyPair::class.java }
            .map { it.get(null) as CurrencyPair }
            .map { currencyPair -> assertThat(currencyPair.toString()).isEqualTo("${currencyPair.base}_${currencyPair.counter}") }
            .count()
        assertThat(properPredefinedCurrencyPairs).isNotZero
        */

            val properPredefinedCurrencyPairs2: Int = CurrencyPair.Companion::class.declaredMemberProperties
                .asSequence()
                .filter { it.isFinal && it.visibility == KVisibility.PUBLIC && it.returnType.javaType == CurrencyPair::class.java }
                .map { it.name to it.get(CurrencyPair.Companion) as CurrencyPair }
                .map { v: Pair<String, CurrencyPair> ->
                    assertThat("${v.second.base}_${v.second.counter}")
                        .describedAs("Seems constant ${v.first} has wrong definition.")
                        .isEqualTo(v.first); v
                }
                .onEach { println("Predefined currency pair ${it.first} is OK.") }
                .count()
            assertThat(properPredefinedCurrencyPairs2).isNotZero

        }.assertAll()
    }

    @Test
    fun suppressUnused() {
        SoftAssertions().apply {

            assertThat(CurrencyPair.USD_EUR.toString()).isEqualTo("USD_EUR")
            assertThat(CurrencyPair.EUR_USD.toString()).isEqualTo("EUR_USD")

            assertThat(CurrencyPair.USD_UAH.toString()).isEqualTo("USD_UAH")
            assertThat(CurrencyPair.UAH_USD.toString()).isEqualTo("UAH_USD")

            assertThat(CurrencyPair.EUR_UAH.toString()).isEqualTo("EUR_UAH")
            assertThat(CurrencyPair.UAH_EUR.toString()).isEqualTo("UAH_EUR")

        }.assertAll()
    }
}
