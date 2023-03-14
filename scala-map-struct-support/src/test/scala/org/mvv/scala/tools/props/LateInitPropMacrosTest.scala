package org.mvv.scala.tools.props

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.{ Test, DisplayName }


class LateInitPropMacrosClassUsingDifferentMacros :
  val vv = 2
  val prop1: String = lhsMacroVarName
  val _prop2: String = lhsMacroVarName
  val prop3_ : String = lhsMacroVarName
  val _prop4_ : String = lhsMacroVarName

  val prop11: String = strippedLhsMacroVarName
  val _prop12: String = strippedLhsMacroVarName
  val prop13_ : String = strippedLhsMacroVarName
  val _prop14_ : String = strippedLhsMacroVarName

  val propVal1: PropertyValue[String] = lateInitGeneralProp[String]
  val _propVal2: PropertyValue[String] = lateInitGeneralProp
  //noinspection TypeAnnotation
  val propVal3_ = lateInitGeneralProp[String]
  //noinspection TypeAnnotation
  val _propVal4_ = lateInitGeneralProp[String]

  //noinspection TypeAnnotation
  val _propVal5 = lateInitOptionProp[String]



//noinspection TypeAnnotation
class LateInitPropMacrosClassUsingUniversalMacros :
  val vv = 2

  val propVal1: PropertyValue[String] = lateInitUnchangeableProp[String]
  val _propVal2: PropertyValue[String] = lateInitUnchangeableProp
  val propVal3_ = lateInitProp[String]
  val _propVal4_ = lateInitUnchangeableProp[String]

  val _propVal5 = lateInitUnchangeableProp[Option[String]]

  val propVal6 = lateInitProp[String]                            // changeable

  val propVal7 = unchangeableProp[String]("123")
  val propVal8 = unchangeableProp[Option[String]](None)



class LeftVarNameOfMacrosTest {
  import scala.language.unsafeNulls

  @Test
  @DisplayName("leftVarName")
  def leftVarNameTest(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingDifferentMacros()
    a.assertThat(obj.prop1).isEqualTo("prop1")
    a.assertThat(obj._prop2).isEqualTo("_prop2")
    a.assertThat(obj.prop3_).isEqualTo("prop3_")
    a.assertThat(obj._prop4_).isEqualTo("_prop4_")
    a.assertAll()
  }

  @Test
  @DisplayName("strippedLeftVarName")
  def strippedLeftVarNameTest(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingDifferentMacros()
    a.assertThat(obj.prop11).isEqualTo("prop11")
    a.assertThat(obj._prop12).isEqualTo("prop12")
    a.assertThat(obj.prop13_).isEqualTo("prop13")
    a.assertThat(obj._prop14_).isEqualTo("prop14")
    a.assertAll()
  }


  @Test
  def lateInitGeneralPropName(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingDifferentMacros()

    a.assertThat(obj.propVal1.name).isEqualTo("propVal1")
    a.assertThat(obj.propVal1.changeable).isFalse

    a.assertThat(obj._propVal2.name).isEqualTo("propVal2")
    a.assertThat(obj._propVal2.changeable).isFalse

    a.assertThat(obj.propVal3_.name).isEqualTo("propVal3")
    a.assertThat(obj.propVal3_.changeable).isFalse

    a.assertThat(obj._propVal4_.name).isEqualTo("propVal4")
    a.assertThat(obj._propVal4_.changeable).isFalse

    a.assertAll()
  }

  @Test
  def lateInitGeneralIsNotChangeable(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingDifferentMacros()

    a.assertThat(obj._propVal4_.name).isEqualTo("propVal4")
    a.assertThat(obj._propVal4_.changeable).isFalse

    a.assertThat(obj._propVal4_.asNullableValue).isNull

    a.assertThatCode { () => obj._propVal4_.value }
      .hasMessage("Property [propVal4] is not initialized.")
      .isExactlyInstanceOf(classOf[UninitializedPropertyAccessException])

    obj._propVal4_.value = "strVal4"

    a.assertThat(obj._propVal4_.asNullableValue).isEqualTo("strVal4")
    a.assertThat(obj._propVal4_.value).isEqualTo("strVal4")

    a.assertThatCode { () => obj._propVal4_.value = "strVal5" }
      .hasMessage("Not allowed to change property [propVal4] (from [strVal4] to [strVal5]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj._propVal4_.value).isEqualTo("strVal4")

    // setting the same value
    a.assertThatCode { () => obj._propVal4_.value = "strVal4" } .doesNotThrowAnyException()
    a.assertThat(obj._propVal4_.value).isEqualTo("strVal4")

    a.assertAll()
  }

  @Test
  def lateInitOptionPropIsNotChangeable(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingDifferentMacros()

    a.assertThat(obj._propVal5.name).isEqualTo("propVal5")
    a.assertThat(obj._propVal5.changeable).isFalse

    a.assertThat(obj._propVal5.asNullableValue).isNotNull.isEqualTo(None)

    a.assertThatCode { () => obj._propVal5.value }
      .hasMessage("Property [propVal5] is not initialized.")
      .isExactlyInstanceOf(classOf[UninitializedPropertyAccessException])

    a.assertThatCode { () => obj._propVal5.value = null.asInstanceOf[Option[String]] }
      .hasMessage("Not allowed to change property [propVal5] (from [None] to [null]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj._propVal5.asNullableValue).isNotNull.isEqualTo(None)

    obj._propVal5.value = Option("strVal4")

    a.assertThat(obj._propVal5.asNullableValue).isEqualTo(Option("strVal4"))
    a.assertThat(obj._propVal5.value).isEqualTo(Option("strVal4"))

    a.assertThatCode { () => obj._propVal5.value = Option("strVal5") }
      .hasMessage("Not allowed to change property [propVal5] (from [Some(strVal4)] to [Some(strVal5)]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj._propVal5.value).isEqualTo(Option("strVal4"))

    // setting the same value
    a.assertThatCode { () => obj._propVal5.value = Option("strVal4") } .doesNotThrowAnyException()
    a.assertThat(obj._propVal5.value).isEqualTo(Option("strVal4"))

    a.assertAll()
  }


  @Test
  def lateInitUniversal_testPropName(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingUniversalMacros()

    a.assertThat(obj.propVal1.name).isEqualTo("propVal1")
    a.assertThat(obj.propVal1.changeable).isFalse

    a.assertThat(obj._propVal2.name).isEqualTo("propVal2")
    a.assertThat(obj._propVal2.changeable).isFalse

    a.assertThat(obj.propVal3_.name).isEqualTo("propVal3")
    a.assertThat(obj.propVal3_.changeable).isTrue

    a.assertThat(obj._propVal4_.name).isEqualTo("propVal4")
    a.assertThat(obj._propVal4_.changeable).isFalse

    a.assertThat(obj._propVal5.name).isEqualTo("propVal5")
    a.assertThat(obj._propVal5.changeable).isFalse

    a.assertAll()
  }

  @Test
  def lateInitUnchangeablePropForGeneralTypeIsNotChangeable(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingUniversalMacros()

    a.assertThat(obj._propVal4_.name).isEqualTo("propVal4")
    a.assertThat(obj._propVal4_.changeable).isFalse

    a.assertThat(obj._propVal4_.asNullableValue).isNull

    a.assertThatCode { () => obj._propVal4_.value }
      .hasMessage("Property [propVal4] is not initialized.")
      .isExactlyInstanceOf(classOf[UninitializedPropertyAccessException])

    obj._propVal4_.value = "strVal4"

    a.assertThat(obj._propVal4_.asNullableValue).isEqualTo("strVal4")
    a.assertThat(obj._propVal4_.value).isEqualTo("strVal4")

    a.assertThatCode { () => obj._propVal4_.value = "strVal5" }
      .hasMessage("Not allowed to change property [propVal4] (from [strVal4] to [strVal5]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj._propVal4_.value).isEqualTo("strVal4")

    // setting the same value
    a.assertThatCode { () => obj._propVal4_.value = "strVal4" } .doesNotThrowAnyException()
    a.assertThat(obj._propVal4_.value).isEqualTo("strVal4")

    a.assertAll()
  }

  @Test
  def lateInitUnchangeablePropForOptionIsNotChangeable(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingUniversalMacros()

    a.assertThat(obj._propVal5.name).isEqualTo("propVal5")
    a.assertThat(obj._propVal5.changeable).isFalse

    a.assertThat(obj._propVal5.asNullableValue).isNotNull.isEqualTo(None)

    a.assertThatCode { () => obj._propVal5.value }
      .hasMessage("Property [propVal5] is not initialized.")
      .isExactlyInstanceOf(classOf[UninitializedPropertyAccessException])

    a.assertThatCode { () => obj._propVal5.value = null.asInstanceOf[Option[String]] }
      .hasMessage("Not allowed to change property [propVal5] (from [None] to [null]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj._propVal5.asNullableValue).isNotNull.isEqualTo(None)

    obj._propVal5.value = Option("strVal4")

    a.assertThat(obj._propVal5.asNullableValue).isEqualTo(Option("strVal4"))
    a.assertThat(obj._propVal5.value).isEqualTo(Option("strVal4"))

    a.assertThatCode { () => obj._propVal5.value = Option("strVal5") }
      .hasMessage("Not allowed to change property [propVal5] (from [Some(strVal4)] to [Some(strVal5)]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj._propVal5.value).isEqualTo(Option("strVal4"))

    // setting the same value
    a.assertThatCode { () => obj._propVal5.value = Option("strVal4") } .doesNotThrowAnyException()
    a.assertThat(obj._propVal5.value).isEqualTo(Option("strVal4"))

    a.assertAll()
  }

  @Test
  def unchangeablePropForNonOptionIsChangeable(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingUniversalMacros()

    a.assertThat(obj.propVal7.name).isEqualTo("propVal7")
    a.assertThat(obj.propVal7.changeable).isFalse

    a.assertThat(obj.propVal7.asNullableValue).isNotNull.isEqualTo("123")
    a.assertThat(obj.propVal7.value).isNotNull.isEqualTo("123")

    a.assertThatCode { () => obj.propVal7.value = null.asInstanceOf[String] }
      .hasMessage("Not allowed to change property [propVal7] (from [123] to [null]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj.propVal7.asNullableValue).isNotNull.isEqualTo("123")

    a.assertThatCode { () => obj.propVal7.value = "strVal4" }
      .hasMessage("Not allowed to change property [propVal7] (from [123] to [strVal4]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])

    a.assertThat(obj.propVal7.asNullableValue).isNotNull.isEqualTo("123")
    a.assertThat(obj.propVal7.value).isEqualTo("123")

    // setting the same value
    a.assertThatCode { () => obj.propVal7.value = "123" } .doesNotThrowAnyException()
    a.assertThat(obj.propVal7.value).isEqualTo("123")

    a.assertAll()
  }

  @Test
  def unchangeablePropForOptionIsChangeableForOption(): Unit = {
    val a = SoftAssertions()
    val obj = LateInitPropMacrosClassUsingUniversalMacros()

    a.assertThat(obj.propVal8.name).isEqualTo("propVal8")
    a.assertThat(obj.propVal8.changeable).isFalse

    a.assertThat(obj.propVal8.asNullableValue).isNotNull.isEqualTo(None)
    a.assertThat(obj.propVal8.value).isNotNull.isEqualTo(None)

    a.assertThatCode { () => obj.propVal8.value = None }
      .doesNotThrowAnyException()

    a.assertThatCode { () => obj.propVal8.value = null.asInstanceOf[Option[String]] }
      .hasMessage("Not allowed to change property [propVal8] (from [None] to [null]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj.propVal8.asNullableValue).isNotNull.isEqualTo(None)

    obj.propVal8.value = Option("strVal4")

    a.assertThat(obj.propVal8.asNullableValue).isEqualTo(Option("strVal4"))
    a.assertThat(obj.propVal8.value).isEqualTo(Option("strVal4"))

    a.assertThatCode { () => obj.propVal8.value = Option("strVal5") }
      .hasMessage("Not allowed to change property [propVal8] (from [Some(strVal4)] to [Some(strVal5)]).")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
    a.assertThat(obj.propVal8.value).isEqualTo(Option("strVal4"))

    // setting the same value
    a.assertThatCode { () => obj.propVal8.value = Option("strVal4") } .doesNotThrowAnyException()
    a.assertThat(obj.propVal8.value).isEqualTo(Option("strVal4"))

    a.assertAll()
  }

}
