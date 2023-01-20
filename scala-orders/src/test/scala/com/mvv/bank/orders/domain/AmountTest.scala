package com.mvv.bank.orders.domain

import scala.language.unsafeNulls
//
import java.math.BigDecimal as jbd
import scala.BigDecimal as bd
//
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AmountTest {

  @Test
  def amountEqual(): Unit = {
    assertThat(Amount(bd("12345.678"), Currency.USD))
      .isEqualTo(Amount(bd("12345.678"), Currency("USD")))

    assertThat(Amount.of(bd("12345.678"), Currency.USD))
      .isEqualTo(Amount(bd("12345.678"), Currency("USD")))
    assertThat(Amount.valueOf(bd("12345.678"), Currency.USD))
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

}
