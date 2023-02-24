package org.mvv.scala.tools.props

import scala.language.unsafeNulls
//
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test



class ReadOnlyPropTest {

  @Test
  def testManuallyCreated(): Unit = {

    var v = 1
    val prop = ReadOnlyProp("v", v)

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("v")
    a.assertThat(prop.value).isEqualTo(1)

    v = 2
    a.assertThat(prop.value).isEqualTo(2)

    a.assertAll()
  }

  @Test
  def testCreatedByMacros(): Unit = {

    var v = 1
    val prop = readOnlyProp(v)

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("v")
    a.assertThat(prop.value).isEqualTo(1)

    v = 2
    a.assertThat(prop.value).isEqualTo(2)

    a.assertAll()
  }
}