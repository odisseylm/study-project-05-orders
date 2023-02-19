package org.mvv.scala.mapstruct.mappers

import org.junit.jupiter.api.Disabled

import scala.language.unsafeNulls
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.quotes.EnumValueSelectMode
import org.mvv.scala.mapstruct.debug.dump.dumpExpr


enum TestEnum1 :
  case TestEnumValue1, TestEnumValue2
enum TestEnum2 :
  case TestEnumValue1, TestEnumValue2


enum TestEnum11 :
  case TestEnumValue1, TestEnumValue2, TestEnumValue3
enum TestEnum12 :
  case TestEnumValue1, TestEnumValue2, TestEnumValue4


enum TestEnum21 :
  case TestEnumValue1, TestEnumValue2, TestEnumValue3
enum TestEnum22 :
  case TestEnumValue1, TestEnumValue2


enum TestEnumWithInheritance1 (intValue: Int) :
  def method1(): Unit = {}
  case TestEnumValue15 extends TestEnumWithInheritance1(15)
  case TestEnumValue16 extends TestEnumWithInheritance1(16)
enum TestEnumWithInheritance2 (intValue: Int) :
  def method2(): Unit = {}
  case TestEnumValue25 extends TestEnumWithInheritance2(25)
  case TestEnumValue26 extends TestEnumWithInheritance2(26)


//noinspection ScalaUnnecessaryParentheses
class EnumMappersTest {

  @Test
  def testUsualEnums_Default(): Unit = {

    val mapFunc: (TestEnum1 => TestEnum2) = enumMappingFunc[TestEnum1, TestEnum2]()

    val a = SoftAssertions()

    a.assertThat(mapFunc(TestEnum1.TestEnumValue1)).isEqualTo(TestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(TestEnum1.TestEnumValue2)).isEqualTo(TestEnum2.TestEnumValue2)

    a.assertAll()
  }

  @Test
  def testUsualEnums_ByEnumFullClassName(): Unit = {

    val mapFunc: (TestEnum1 => TestEnum2) =
      enumMappingFunc[TestEnum1, TestEnum2](EnumValueSelectMode.ByEnumFullClassName)

    val a = SoftAssertions()

    a.assertThat(mapFunc(TestEnum1.TestEnumValue1)).isEqualTo(TestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(TestEnum1.TestEnumValue2)).isEqualTo(TestEnum2.TestEnumValue2)

    a.assertAll()
  }

  @Test
  def testUsualEnums_ByEnumClassThisType(): Unit = {

    val mapFunc: (TestEnum1 => TestEnum2) =
      enumMappingFunc[TestEnum1, TestEnum2](EnumValueSelectMode.ByEnumClassThisType)

    val a = SoftAssertions()

    a.assertThat(mapFunc(TestEnum1.TestEnumValue1)).isEqualTo(TestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(TestEnum1.TestEnumValue2)).isEqualTo(TestEnum2.TestEnumValue2)

    a.assertAll()
  }

  @Test
  def testLocalEnums_ByEnumFullClassName(): Unit = {
    import EnumMappersTest.LocalTestEnum1
    import EnumMappersTest.LocalTestEnum2

    val mapFunc: (EnumMappersTest.LocalTestEnum1 => LocalTestEnum2) =
      enumMappingFunc[LocalTestEnum1, LocalTestEnum2](EnumValueSelectMode.ByEnumFullClassName)

    val a = SoftAssertions()

    a.assertThat(mapFunc(LocalTestEnum1.TestEnumValue1)).isEqualTo(LocalTestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(LocalTestEnum1.TestEnumValue2)).isEqualTo(LocalTestEnum2.TestEnumValue2)

    a.assertAll()
  }

  @Test
  def testLocalEnums_ByEnumClassThisType(): Unit = {
    import EnumMappersTest.LocalTestEnum1
    import EnumMappersTest.LocalTestEnum2

    val mapFunc: (EnumMappersTest.LocalTestEnum1 => LocalTestEnum2) =
      enumMappingFunc[LocalTestEnum1, LocalTestEnum2](EnumValueSelectMode.ByEnumClassThisType)

    val a = SoftAssertions()

    a.assertThat(mapFunc(LocalTestEnum1.TestEnumValue1)).isEqualTo(LocalTestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(LocalTestEnum1.TestEnumValue2)).isEqualTo(LocalTestEnum2.TestEnumValue2)

    a.assertAll()
  }

  // it causes compilation error as expected
  // ?? How to test it? ??
  /*
  @Test
  def testAsymmetricEnums(): Unit = {
    val mapFunc: (TestEnum11 => TestEnum12) = enumMappingFunc[TestEnum11, TestEnum12]()

    val a = SoftAssertions()

    a.assertThat(mapFunc(TestEnum11.TestEnumValue1)).isEqualTo(TestEnum12.TestEnumValue1)
    a.assertThat(mapFunc(TestEnum11.TestEnumValue2)).isEqualTo(TestEnum12.TestEnumValue1)

    a.assertAll()
  }
  */

  @Test
  def testAsymmetricEnums(): Unit = {

    val a = SoftAssertions()
    //val aa: (TestEnum11, TestEnum12) = dumpExpr( (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4) )


    //val aa1: String = (TestEnum11.TestEnumValue3 => TestEnum12.TestEnumValue4)
    //val aa2: TestEnum11 => TestEnum12 = TestEnum11.TestEnumValue3 => TestEnum12.TestEnumValue4

    val mapFunc: (TestEnum11 => TestEnum12) = enumMappingFunc[TestEnum11, TestEnum12](
      EnumValueSelectMode.ByEnumFullClassName,
      (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4),
    )

    a.assertThat(mapFunc(TestEnum11.TestEnumValue1)).isEqualTo(TestEnum12.TestEnumValue1)
    a.assertThat(mapFunc(TestEnum11.TestEnumValue2)).isEqualTo(TestEnum12.TestEnumValue2)
    a.assertThat(mapFunc(TestEnum11.TestEnumValue3)).isEqualTo(TestEnum12.TestEnumValue4)

    a.assertAll()
  }

  @Test
  def testAsymmetricComplexEnums(): Unit = {

    val a = SoftAssertions()

    val mapFunc: (TestEnumWithInheritance1 => TestEnumWithInheritance2) =
      enumMappingFunc[TestEnumWithInheritance1, TestEnumWithInheritance2](
        EnumValueSelectMode.ByEnumFullClassName,
        (TestEnumWithInheritance1.TestEnumValue15, TestEnumWithInheritance2.TestEnumValue25),
        (TestEnumWithInheritance1.TestEnumValue16, TestEnumWithInheritance2.TestEnumValue26),
    )

    a.assertThat(mapFunc(TestEnumWithInheritance1.TestEnumValue15))
      .isEqualTo(TestEnumWithInheritance2.TestEnumValue25)
    a.assertThat(mapFunc(TestEnumWithInheritance1.TestEnumValue16))
      .isEqualTo(TestEnumWithInheritance2.TestEnumValue26)

    a.assertAll()
  }

  @Test
  def testPassingCustomMappingsAsRepeatedParam(): Unit = {
    _internalTestCustomMappingsAsRepeatedParams[TestEnum11, TestEnum12](
      (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4)
    )

    _internalTestCustomMappingsAsRepeatedParams[TestEnum11, TestEnum12](
      (TestEnum11.TestEnumValue1, TestEnum12.TestEnumValue2),
      (TestEnum11.TestEnumValue2, TestEnum12.TestEnumValue1),
      (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4),
    )
  }

  @Test
  def testPassingCustomMappingsAsListParam(): Unit = {
    _internalTestCustomMappingsAsListParam[TestEnum11, TestEnum12](
      List( (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4) )
    )

    _internalTestCustomMappingsAsListParam[TestEnum11, TestEnum12](
      List(
        (TestEnum11.TestEnumValue1, TestEnum12.TestEnumValue2),
        (TestEnum11.TestEnumValue2, TestEnum12.TestEnumValue1),
        (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4),
      )
    )
  }

  @Test
  def testPassingCustomMappingsAsListParamUsingOperators(): Unit = {
    //noinspection ScalaUnusedSymbol
    val temp01: List[String] = "s1" :: "s2" :: "s3" :: Nil

    //noinspection ScalaUnusedSymbol
    val temp02: List[(TestEnum11, TestEnum12)] =
           (TestEnum11.TestEnumValue1, TestEnum12.TestEnumValue2)
        :: (TestEnum11.TestEnumValue2, TestEnum12.TestEnumValue1)
        :: (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4)
        :: Nil

    _internalTestCustomMappingsAsListParam[TestEnum11, TestEnum12](
           (TestEnum11.TestEnumValue1, TestEnum12.TestEnumValue2)
        :: (TestEnum11.TestEnumValue2, TestEnum12.TestEnumValue1)
        :: (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4)
        :: Nil
    )
  }

  @Test
  def testPassingCustomMappingsAsSingleParam(): Unit = {
    _internalTestCustomMappingsAsSingleParam[TestEnum11, TestEnum12](
      (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4)
    )
  }
}

def usingLocalClasses(): Unit = {
  val enumValue1 = EnumMappersTest.LocalTestEnum1.TestEnumValue1
  println(s"enumValue1: $enumValue1")
}


// "static" classes should be placed inside the companion object
object EnumMappersTest :

  enum LocalTestEnum1 :
    case TestEnumValue1, TestEnumValue2

  enum LocalTestEnum2 :
    case TestEnumValue1, TestEnumValue2
