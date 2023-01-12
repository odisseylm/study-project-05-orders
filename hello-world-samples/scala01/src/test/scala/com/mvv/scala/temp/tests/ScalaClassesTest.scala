package com.mvv.scala.temp.tests

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat


class ScalaClassesTest {

  @Test
  @DisplayName("matchTest02")
  def testMatchTest02(): Unit = {
    assertThat(matchTest02(CaseClass1("qwerty"))).isEqualTo("qwerty")
    assertThat(matchTest02(CaseClass1("qwerty"))).isEqualTo("qwerty")
  }
}
