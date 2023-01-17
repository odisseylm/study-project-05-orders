package com.mvv.bank.orders.domain

import scala.language.unsafeNulls
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.{assertThat, assertThatCode}
import org.junit.jupiter.api.{Disabled, Test}


class CurrenciesTest {

  @Test
  def currency(): Unit = {
    assertThat(Currency.of("USD")).isEqualTo(Currency.USD)
    assertThat(Currency.of("USD").value).isEqualTo("USD")

    assertThatCode ( () => Currency.of(null) )
      .hasMessage("Invalid currency [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency.of("U") )
      .hasMessage("Invalid currency [U].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency.of("QWERTY") )
      .hasMessage("Invalid currency [QWERTY].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency.of("usd") )
      .hasMessage("Invalid currency [usd].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency.of(" USD ") )
      .hasMessage("Invalid currency [ USD ].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
  }


  @Test
  def currencyPair(): Unit =
    assertThat(CurrencyPair.of("USD_EUR").toString).isEqualTo("USD_EUR")
    assertThat(CurrencyPair.of("USD_EUR")).isEqualTo(CurrencyPair.USD_EUR)

    assertThatCode ( () => CurrencyPair.of(null) )
      .hasMessage("Invalid currency pair [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => CurrencyPair.of("U") )
      .hasMessage("Invalid currency pair [U] (length should be 7).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

  end currencyPair


  @Test
  @Disabled("For manual debugging")
  def temp(): Unit = {
    assertThatCode ( () => CurrencyPair.of("U") )
      .hasMessage("Invalid currency [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
  }
}
