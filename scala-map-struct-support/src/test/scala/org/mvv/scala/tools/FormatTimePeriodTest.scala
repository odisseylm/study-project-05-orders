package org.mvv.scala.tools

import scala.language.unsafeNulls
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.{ Test, DisplayName }



class FormatTimePeriodTest {

  @Test
  @DisplayName("formatTimePeriod")
  def formatTimePeriodTest(): Unit = {

    val a = SoftAssertions()

    a.assertThat(formatTimePeriod(5)).isEqualTo("5ns")
    a.assertThat(formatTimePeriod(50)).isEqualTo("50ns")
    a.assertThat(formatTimePeriod(500)).isEqualTo("500ns")

    a.assertThat(formatTimePeriod(1_500)).isEqualTo("1.500mcs")
    a.assertThat(formatTimePeriod(10_500)).isEqualTo("10.500mcs")
    a.assertThat(formatTimePeriod(100_500)).isEqualTo("100.500mcs")
    a.assertThat(formatTimePeriod(999_500)).isEqualTo("999.500mcs")

    a.assertThat(formatTimePeriod(1_050_010L)).isEqualTo("1.050ms")
    a.assertThat(formatTimePeriod(10_050_010L)).isEqualTo("10.050ms")
    a.assertThat(formatTimePeriod(100_050_010L)).isEqualTo("100.050ms")
    a.assertThat(formatTimePeriod(999_050_010L)).isEqualTo("999.050ms")

    a.assertThat(formatTimePeriod(1_000_500_000L)).isEqualTo("1.0s")
    a.assertThat(formatTimePeriod(1_030_500_000L)).isEqualTo("1.030s")
    a.assertThat(formatTimePeriod(10_030_500_000L)).isEqualTo("10.030s")
    a.assertThat(formatTimePeriod(59_030_500_000L)).isEqualTo("59.030s")
    a.assertThat(formatTimePeriod(60_030_500_000L)).isEqualTo("1:00")
    a.assertThat(formatTimePeriod(66_030_500_000L)).isEqualTo("1:06")
    a.assertThat(formatTimePeriod(119_030_500_000L)).isEqualTo("1:59")
    a.assertThat(formatTimePeriod(120_030_500_000L)).isEqualTo("2:00")

    a.assertAll()

  }

}