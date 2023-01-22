package com.mvv.bank.orders.domain

import scala.language.unsafeNulls
//
import java.math.BigDecimal as jbd
import scala.BigDecimal as bd
//
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.{ Test, DisplayName }
//
import com.mvv.bank.orders.domain.Currency.{ USD, EUR }
import com.mvv.test.SoftAssertions.runTests
import com.mvv.test.Tests.run


class AmountTest {

  @Test
  def amountEqual(): Unit = {
    assertThat(Amount(bd("12345.678"), Currency.USD))
      .isEqualTo(Amount(bd("12345.678"), Currency("USD")))

    assertThat(Amount.of(bd("12345.678"), Currency.USD))
      .isEqualTo(Amount(bd("12345.678"), Currency("USD")))
    assertThat(Amount.valueOf("12345.678 USD"))
      .isEqualTo(Amount(bd("12345.678"), Currency("USD")))

    assertThat(Amount(bd("12345.678"), Currency.USD))
      .isEqualTo(Amount(bd("12345.678"), Currency("USD")))

    assertThat(Amount(bd("2"), Currency.USD))
      .isNotEqualTo(Amount(bd("2"), Currency.EUR))
  }

  @Test
  def javaBigDecimalBehavior(): Unit = {
    assertThat(jbd("12345.67")).isEqualTo(jbd("12345.67"))
    assertThat(jbd("12345.67").hashCode).isEqualTo(jbd("12345.67").hashCode)
    
    assertThat(jbd("12345.67")).isNotEqualTo(jbd("12345.6701"))
    assertThat(jbd("12345.67").hashCode).isNotEqualTo(jbd("12345.6701").hashCode)

    // in java they are NOT equal
    assertThat(jbd("12345.67")).isNotEqualTo(jbd("12345.6700"))
    assertThat(jbd("12345.67").hashCode).isNotEqualTo(jbd("12345.6700").hashCode)

    // in java they are NOT equal
    assertThat(jbd("3e5")).isNotEqualTo(jbd("30E4"))
    assertThat(jbd("3E5").hashCode).isNotEqualTo(jbd("30e4").hashCode)
  }

  @Test
  def scalaBigDecimalBehavior(): Unit = {
    assertThat(bd("12345.67")).isEqualTo(bd("12345.67"))
    assertThat(bd("12345.67").hashCode).isEqualTo(bd("12345.67").hashCode)

    assertThat(bd("12345.67")).isNotEqualTo(bd("12345.6701"))
    assertThat(bd("12345.67").hashCode).isNotEqualTo(bd("12345.6701").hashCode)

    // in scala they are DO equal
    assertThat(bd("12345.67")).isEqualTo(bd("12345.6700"))
    assertThat(bd("12345.67").hashCode).isEqualTo(bd("12345.6700").hashCode)
    assertThat(bd("3E5").hashCode).isEqualTo(bd("30e4").hashCode)
  }

  @Test
  def amountEqualWithOnlyDifferentPrecision(): Unit = {
    assertThat(Amount(bd("12345.67"), Currency.USD))
      .isEqualTo(Amount(bd("12345.67000000"), Currency.USD))
    assertThat(Amount(bd("12345.67"), Currency.USD).hashCode())
      .isEqualTo(Amount(bd("12345.67000000"), Currency.USD).hashCode())

    assertThat(Amount(bd("3e5"), Currency.USD))
      .isEqualTo(Amount(bd("30E4"), Currency.USD))
    assertThat(Amount(bd("3E5"), Currency.USD).hashCode())
      .isEqualTo(Amount(bd("30e4"), Currency.USD).hashCode())
  }


  @Test
  def create(): Unit =  {

      SoftAssertions().runTests { a =>

          run {
              val amount = Amount(bd("12.34"), Currency("USD"))
              a.assertThat(amount.value).isEqualTo(bd("12.34"))
              a.assertThat(amount.currency).isEqualTo(Currency("USD"))
              a.assertThat(amount.currency.value).isEqualTo("USD")
          }

          run {
              val amount = Amount(bd("12.34"), Currency("USD"))
              a.assertThat(amount.value).isEqualTo(bd("12.34"))
              a.assertThat(amount.currency).isEqualTo(Currency("USD"))
              a.assertThat(amount.currency.value).isEqualTo("USD")
          }

          run {
              val amount = Amount(bd("12.34"), USD)
              a.assertThat(amount.value).isEqualTo(bd("12.34"))
              a.assertThat(amount.currency).isEqualTo(Currency("USD"))
              a.assertThat(amount.currency.value).isEqualTo("USD")
          }

          run {
              val amount = Amount.valueOf("12.34 USD")
              a.assertThat(amount.value).isEqualTo(bd("12.34"))
              a.assertThat(amount.currency).isEqualTo(Currency("USD"))
              a.assertThat(amount.currency.value).isEqualTo("USD")
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
  def compare(): Unit =  {
      SoftAssertions().runTests { a =>

          a.assertThat(Amount(bd("1234.5"), USD)).isEqualTo(Amount(bd("1234.5"), USD))
          a.assertThat(Amount(bd("1234.5"), USD)).isNotEqualTo(Amount(bd("1234.5"), EUR))

          // with another precision
          a.assertThat(Amount(bd("1234.5"), USD)).isEqualTo(Amount(bd("1234.50"), USD))

      }.assertAll()
  }

  @Test
  @DisplayName("hashCode")
  def testHashCode(): Unit =  {
      SoftAssertions().runTests { a =>

          println(bd("30000").scale)
          println(bd("30000").precision)
          println(bd("30000.00").scale)
          println(bd("30000.00").precision)
          println(bd("0.001").scale)
          println(bd("0.001").precision)
          println(bd("3e5").scale)
          println(bd("3e5").precision)

          a.assertThat(Amount(bd("0"), USD).hashCode()).isEqualTo(Amount(bd("0"), USD).hashCode())
          a.assertThat(Amount(bd("1234.5"), USD).hashCode()).isEqualTo(Amount(bd("1234.5"), USD).hashCode())
          a.assertThat(Amount(bd("1234.5"), USD).hashCode()).isNotEqualTo(Amount(bd("1234.5"), EUR).hashCode())

          // with another precision
          a.assertThat(Amount(bd("1234.5"), USD).hashCode()).isEqualTo(Amount(bd("1234.5000"), USD).hashCode())
          a.assertThat(Amount(bd("3e5"), USD).hashCode()).isEqualTo(Amount(bd("30e4"), USD).hashCode())

      }.assertAll()
  }

}
