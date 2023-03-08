package org.mvv.scala.tools.props

import scala.language.unsafeNulls
//
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test



class WritablePropTest {

  @Test
  def testManuallyCreated(): Unit = {

    var v = 1
    val prop = WritableProp("v", v, vv => v = vv)

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("v")
    a.assertThat(prop.value).isEqualTo(1)

    v = 2
    a.assertThat(prop.value).isEqualTo(2)

    prop.value = 3
    a.assertThat(prop.value).isEqualTo(3)
    a.assertThat(v).isEqualTo(3)

    a.assertAll()
  }

  @Test
  def testPropOverLocalVarCreatedByMacros(): Unit = {

    var v = 1
    val prop = writableProp(v)

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("v")
    a.assertThat(prop.value).isEqualTo(1)

    v = 2
    a.assertThat(prop.value).isEqualTo(2)

    prop.value = 3
    a.assertThat(prop.value).isEqualTo(3)
    a.assertThat(v).isEqualTo(3)

    a.assertAll()
  }

  /*
  @Test
  def testPropOverLocalVarCreatedByMacrosForImmutableVar(): Unit =
    val v = 1
    writableProp(v) // should be compilation error
  */

  @Test
  def testPropOverLocalVarCreatedByMacros_byAssign(): Unit = {

    var v = 1
    val prop = writableProp(v, SetPropValueMode.ByAssign)

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("v")
    a.assertThat(prop.value).isEqualTo(1)

    v = 2
    a.assertThat(prop.value).isEqualTo(2)

    prop.value = 3
    a.assertThat(prop.value).isEqualTo(3)
    a.assertThat(v).isEqualTo(3)

    a.assertAll()
  }

  /*
  //noinspection VarCouldBeVal
  @Test
  def testPropOverLocalVarCreatedByMacros_byFieldAccessorMethod(): Unit =
    var v = 1
    writableProp(v, SetPropValueMode.ByFieldAccessorMethod) // compilation should be failed
  */

  @Test
  def testPropOfClassCreatedByMacros(): Unit = {

    val obj = WritablePropTestClass()
    obj.prop1 = 1
    val prop = obj.propWrapper

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("WritablePropTestClass.this.prop1")
    a.assertThat(prop.value).isEqualTo(1)

    obj.prop1 = 2
    a.assertThat(prop.value).isEqualTo(2)

    prop.value = 3
    a.assertThat(prop.value).isEqualTo(3)
    a.assertThat(obj.prop1).isEqualTo(3)

    a.assertAll()
  }

  @Test
  def testPropOfClassCreatedByMacros_ByAssign(): Unit = {

    val obj = WritablePropTestClassByAssign()
    obj.prop1 = 1
    val prop = obj.propWrapper

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("WritablePropTestClassByAssign.this.prop1")
    a.assertThat(prop.value).isEqualTo(1)

    obj.prop1 = 2
    a.assertThat(prop.value).isEqualTo(2)

    prop.value = 3
    a.assertThat(prop.value).isEqualTo(3)
    a.assertThat(obj.prop1).isEqualTo(3)

    a.assertAll()
  }

  @Test
  def testPropOfClassCreatedByMacros_ByFieldAccessorMethod(): Unit = {

    val obj = WritablePropTestClassByFieldAccessorMethod()
    obj.prop1 = 1
    val prop = obj.propWrapper

    val a =  SoftAssertions()

    a.assertThat(prop.name).isEqualTo("WritablePropTestClassByFieldAccessorMethod.this.prop1")
    a.assertThat(prop.value).isEqualTo(1)

    obj.prop1 = 2
    a.assertThat(prop.value).isEqualTo(2)

    prop.value = 3
    a.assertThat(prop.value).isEqualTo(3)
    a.assertThat(obj.prop1).isEqualTo(3)

    a.assertAll()
  }

}


class WritablePropTestClass :
  var prop1: Int = 0
  val propWrapper: WritableProp[Int] = writableProp(prop1)

class WritablePropTestClassByAssign :
  var prop1: Int = 0
  val propWrapper: WritableProp[Int] = writableProp(prop1, SetPropValueMode.ByAssign)

class WritablePropTestClassByFieldAccessorMethod :
  var prop1: Int = 0
  val propWrapper: WritableProp[Int] = writableProp(prop1, SetPropValueMode.ByFieldAccessorMethod)
