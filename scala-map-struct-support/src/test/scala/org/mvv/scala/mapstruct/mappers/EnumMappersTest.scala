package org.mvv.scala.mapstruct.mappers

import scala.language.unsafeNulls
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions


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
      enumMappingFunc[TestEnum1, TestEnum2](SelectEnumMode.ByEnumFullClassName)

    val a = SoftAssertions()

    a.assertThat(mapFunc(TestEnum1.TestEnumValue1)).isEqualTo(TestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(TestEnum1.TestEnumValue2)).isEqualTo(TestEnum2.TestEnumValue2)

    a.assertAll()
  }

  @Test
  def testUsualEnums_ByEnumClassThisType(): Unit = {

    val mapFunc: (TestEnum1 => TestEnum2) =
      enumMappingFunc[TestEnum1, TestEnum2](SelectEnumMode.ByEnumClassThisType)

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
      enumMappingFunc[LocalTestEnum1, LocalTestEnum2](SelectEnumMode.ByEnumFullClassName)

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
      enumMappingFunc[LocalTestEnum1, LocalTestEnum2](SelectEnumMode.ByEnumClassThisType)

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
