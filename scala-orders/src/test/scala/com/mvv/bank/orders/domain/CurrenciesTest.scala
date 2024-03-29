package com.mvv.bank.orders.domain

import scala.language.unsafeNulls
import org.assertj.core.api.{ Assertions, SoftAssertions }
import org.assertj.core.api.Assertions.{assertThat, assertThatCode}
import org.junit.jupiter.api.{ Test, Disabled }


class CurrenciesTest {

  @Test
  def temp33(): Unit = {
    assertThat(Currency("USD")).isEqualTo(Currency.USD)

    val a = SoftAssertions()

    a.assertThatCode(() => Currency(null))
      .hasMessage("Invalid currency [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    a.assertThatCode { () => Currency(null) }
      .hasMessage("Invalid currency [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    /*
    a.assertThatCode(() => Currency(null))
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
      .hasMessage("Invalid currency [null].")

    a.assertThatCode { () => Currency(null) }
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
      .hasMessage("Invalid currency [null].")
    */

    a.assertAll()
  }

  @Test
  def currency(): Unit = {
    assertThat(Currency("USD")).isEqualTo(Currency.USD)
    assertThat(Currency.of("USD")).isEqualTo(Currency.USD)
    assertThat(Currency.valueOf("USD")).isEqualTo(Currency.USD)
    assertThat(Currency("USD").value).isEqualTo("USD")

    assertThatCode ( () => Currency(null) )
      .hasMessage("Invalid currency [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency("U") )
      .hasMessage("Invalid currency [U].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency("QWERTY") )
      .hasMessage("Invalid currency [QWERTY].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency("usd") )
      .hasMessage("Invalid currency [usd].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => Currency(" USD ") )
      .hasMessage("Invalid currency [ USD ].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
  }


  @Test
  def currencyPair(): Unit =
    assertThat(CurrencyPair("USD", "EUR").toString).isEqualTo("USD_EUR")
    assertThat(CurrencyPair.of("USD", "EUR").toString).isEqualTo("USD_EUR")
    assertThat(CurrencyPair.of(Currency("USD"), Currency("EUR")).toString).isEqualTo("USD_EUR")
    assertThat(CurrencyPair.of("USD_EUR").toString).isEqualTo("USD_EUR")
    assertThat(CurrencyPair.valueOf("USD_EUR")).isEqualTo(CurrencyPair.USD_EUR)

    assertThatCode ( () => CurrencyPair.valueOf(null) )
      .hasMessage("Invalid currency pair [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    assertThatCode ( () => CurrencyPair.of("U") )
      .hasMessage("Invalid currency pair [U] (length should be 7).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

  end currencyPair


  @Test
  @Disabled("For manual debugging")
  def temp(): Unit = {
    assertThatCode ( () => CurrencyPair.valueOf("U") )
      .hasMessage("Invalid currency [null].")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
  }
}
