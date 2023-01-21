package com.mvv.nullables

import org.junit.jupiter.api.DisplayName

import scala.language.unsafeNulls
//
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test



class NullablesTest {

  @Test
  def testAreNulls(): Unit = {
    assertThat(null.asInstanceOf[AnyRef]).isNull()

    assertThat(null.isNull).isTrue
    val nullStr: String = null
    assertThat(nullStr.isNull).isTrue

    assertThat("".isNull).isFalse
    val notNullStr: String = ""
    assertThat(notNullStr.isNull).isFalse
  }

  @Test
  def testAreNotNulls(): Unit = {
    assertThat("".asInstanceOf[AnyRef]).isNotNull

    assertThat(null.isNotNull).isFalse
    val nullStr: String = null
    assertThat(nullStr.isNotNull).isFalse

    assertThat("".isNotNull).isTrue
    val notNullStr: String = ""
    assertThat(notNullStr.isNotNull).isTrue
  }

  @Test
  @DisplayName("isNullableType")
  def testIsNullablePureType(): Unit = {
    assertThat(isNullablePureType(10)).isFalse

    assertThat(isNullablePureType(null)).isTrue
    assertThat(isNullablePureType("")).isTrue
  }

}
