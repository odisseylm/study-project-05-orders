package org.mvv.scala.tools.props

import scala.language.unsafeNulls
import org.assertj.core.api.Assertions.assertThat
import org.mvv.scala.mapstruct.debug.dump.dumpExpr


private val globalVal1 = 125
//noinspection TypeAnnotation
val globalVal1NamedValueForGlobalVarWithImplicitType = namedValue(globalVal1)
val globalVal1NamedValueForGlobalVarWithExplicitType: NamedValue[Int] = namedValue(globalVal1)


class NamedValueTest {
  val v = 11

  def testLocalNamedValue(): Unit = {
    val var1 = 123

    //val ccc1: org.mvv.scala.tools.props.NamedValue[Int] =
    //  dumpExpr( org.mvv.scala.tools.props.NamedValue[Int]("prop1", var1) )

    val localNamedValueForLocalVarWithImplicitType = namedValue(var1)
    val localNamedValueForLocalVarWithExplicitType: NamedValue[Int] = namedValue(var1)

    assertThat(localNamedValueForLocalVarWithImplicitType.value)
      .isEqualTo(123)
    assertThat(localNamedValueForLocalVarWithImplicitType.name)
      .isEqualTo("var1")


    assertThat(localNamedValueForLocalVarWithExplicitType.value)
      .isEqualTo(123)
    assertThat(localNamedValueForLocalVarWithExplicitType.name)
      .isEqualTo("var1")
  }

  private val field1 = 124
  //noinspection TypeAnnotation
  val fieldNamedValueForLocalVarWithImplicitType = namedValue(field1)
  val fieldNamedValueForLocalVarWithExplicitType: NamedValue[Int] = namedValue(field1)

  def testFieldNamedValueForField(): Unit = {
    assertThat(fieldNamedValueForLocalVarWithImplicitType.value)
      .isEqualTo(124)
    assertThat(fieldNamedValueForLocalVarWithImplicitType.name)
      .isEqualTo("field1")

    assertThat(fieldNamedValueForLocalVarWithExplicitType.value)
      .isEqualTo(124)
    assertThat(fieldNamedValueForLocalVarWithExplicitType.name)
      .isEqualTo("field1")
  }


  def testLocalNamedValueForField(): Unit = {
    val localNamedValueForGlobalVarWithImplicitType = namedValue(field1)
    val localNamedValueForGlobalVarWithExplicitType: NamedValue[Int] = namedValue(field1)

    assertThat(localNamedValueForGlobalVarWithImplicitType.value)
      .isEqualTo(124)
    assertThat(localNamedValueForGlobalVarWithImplicitType.name)
      .isEqualTo("field1")

    assertThat(localNamedValueForGlobalVarWithExplicitType.value)
      .isEqualTo(124)
    assertThat(localNamedValueForGlobalVarWithExplicitType.name)
      .isEqualTo("field1")
  }


  def testGlobalNamedValueForGlobalVal(): Unit = {
    val localNamedValueForGlobalVarWithImplicitType = namedValue(globalVal1)
    val localNamedValueForGlobalVarWithExplicitType: NamedValue[Int] = namedValue(globalVal1)

    assertThat(localNamedValueForGlobalVarWithImplicitType.value)
      .isEqualTo(123)
    assertThat(localNamedValueForGlobalVarWithImplicitType.name)
      .isEqualTo("var1")

    assertThat(localNamedValueForGlobalVarWithExplicitType.value)
      .isEqualTo(123)
    assertThat(localNamedValueForGlobalVarWithExplicitType.name)
      .isEqualTo("var1")
  }


  def testLocalNamedValueForGlobalVal(): Unit = {
    val localNamedValueForGlobalVarWithImplicitType = namedValue(globalVal1)
    val localNamedValueForGlobalVarWithExplicitType: NamedValue[Int] = namedValue(globalVal1)

    assertThat(localNamedValueForGlobalVarWithImplicitType.value)
      .isEqualTo(123)
    assertThat(localNamedValueForGlobalVarWithImplicitType.name)
      .isEqualTo("var1")

    assertThat(localNamedValueForGlobalVarWithExplicitType.value)
      .isEqualTo(123)
    assertThat(localNamedValueForGlobalVarWithExplicitType.name)
      .isEqualTo("var1")
  }

}
