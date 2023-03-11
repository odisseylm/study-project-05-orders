package org.mvv.scala.tools.quotes

import scala.language.unsafeNulls
import org.junit.jupiter.api.{DisplayName, Test}
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions


private val currentPackageGlobal = currentPackage
private val topClassOrModuleFullNameGlobal = topClassOrModuleFullName


class TestCurrentPackageSampleClass :
  val currentPackageVal: String = currentPackage
  val topClassOrModuleFullNameVal: String = topClassOrModuleFullName
object TestCurrentPackageSampleObject :
  val currentPackageVal: String = currentPackage
  val topClassOrModuleFullNameVal: String = topClassOrModuleFullName



class CurrentPackageTest {
  // counter for compilation only
  val number = 19

  @Test
  def currentPackageAsGlobalVar(): Unit =
    assertThat(currentPackageGlobal).isEqualTo("org.mvv.scala.tools.quotes")

  @Test
  def currentPackageInsideMethod(): Unit =
    val currentPackageVal = currentPackage
    assertThat(currentPackageVal).isEqualTo("org.mvv.scala.tools.quotes")

  @Test
  def currentPackageInClass(): Unit =
    assertThat(TestCurrentPackageSampleClass().currentPackageVal).isEqualTo("org.mvv.scala.tools.quotes")

  @Test
  def currentPackageInObject(): Unit =
    assertThat(TestCurrentPackageSampleObject.currentPackageVal).isEqualTo("org.mvv.scala.tools.quotes")
}


class NamesTest {

  @Test
  @DisplayName("classNameOf")
  def classNameOfTest(): Unit =
    val a = SoftAssertions()

    a.assertThat(classNameOf[TestCurrentPackageSampleClass]).isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")
    a.assertThat(classNameOf[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]).isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")

    a.assertAll()

  @Test
  @DisplayName("simpleClassNameOf")
  def simpleClassNameOfTest(): Unit =
    val a = SoftAssertions()

    a.assertThat(simpleClassNameOf[TestCurrentPackageSampleClass]).isEqualTo("TestCurrentPackageSampleClass")
    a.assertThat(simpleClassNameOf[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]).isEqualTo("TestCurrentPackageSampleClass")

    a.assertAll()

  @Test
  @DisplayName("typeNameOf")
  def typeNameOfTest(): Unit =
    val a = SoftAssertions()

    a.assertThat(typeNameOf[TestCurrentPackageSampleClass])
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")
    a.assertThat(typeNameOf[List[TestCurrentPackageSampleClass]])
      .isEqualTo("scala.collection.immutable.List[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")
    a.assertThat(typeNameOf[Option[TestCurrentPackageSampleClass]])
      .isEqualTo("scala.Option[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")

    a.assertThat(simpleTypeNameOf[TestCurrentPackageSampleClass])
      .isEqualTo("TestCurrentPackageSampleClass")
    a.assertThat(simpleTypeNameOf[List[TestCurrentPackageSampleClass]])
      .isEqualTo("List[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")
    a.assertThat(simpleTypeNameOf[Option[TestCurrentPackageSampleClass]])
      .isEqualTo("Option[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")

    a.assertThat(underlyingTypeNameOf[TestCurrentPackageSampleClass])
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")
    a.assertThat(underlyingTypeNameOf[List[TestCurrentPackageSampleClass]])
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")
    a.assertThat(underlyingTypeNameOf[Option[TestCurrentPackageSampleClass]])
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")

    a.assertThat(underlyingSimpleTypeNameOf[TestCurrentPackageSampleClass])
      .isEqualTo("TestCurrentPackageSampleClass")
    a.assertThat(underlyingSimpleTypeNameOf[List[TestCurrentPackageSampleClass]])
      .isEqualTo("TestCurrentPackageSampleClass")
    a.assertThat(underlyingSimpleTypeNameOf[Option[TestCurrentPackageSampleClass]])
      .isEqualTo("TestCurrentPackageSampleClass")

    a.assertAll()

  @Test
  @DisplayName("typeNameOfExpr")
  def typeNameOfExprTest(): Unit =
    val a = SoftAssertions()

    a.assertThat(typeNameOf(TestCurrentPackageSampleClass()))
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")
    a.assertThat(typeNameOf(List(TestCurrentPackageSampleClass())))
      .isEqualTo("scala.collection.immutable.List[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")
    a.assertThat(typeNameOf(Option(TestCurrentPackageSampleClass())))
      .isEqualTo("scala.Option[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")

    a.assertThat(simpleTypeNameOf(TestCurrentPackageSampleClass()))
      .isEqualTo("TestCurrentPackageSampleClass")
    a.assertThat(simpleTypeNameOf(List(TestCurrentPackageSampleClass())))
      .isEqualTo("List[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")
    a.assertThat(simpleTypeNameOf(Option(TestCurrentPackageSampleClass())))
      .isEqualTo("Option[org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass]")

    a.assertThat(underlyingTypeNameOf(TestCurrentPackageSampleClass()))
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")
    a.assertThat(underlyingTypeNameOf(List(TestCurrentPackageSampleClass())))
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")
    a.assertThat(underlyingTypeNameOf(Option(TestCurrentPackageSampleClass())))
      .isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")

    a.assertThat(underlyingSimpleTypeNameOf(TestCurrentPackageSampleClass()))
      .isEqualTo("TestCurrentPackageSampleClass")
    a.assertThat(underlyingSimpleTypeNameOf(List(TestCurrentPackageSampleClass())))
      .isEqualTo("TestCurrentPackageSampleClass")
    a.assertThat(underlyingSimpleTypeNameOf(Option(TestCurrentPackageSampleClass())))
      .isEqualTo("TestCurrentPackageSampleClass")

    val asVal = Option(TestCurrentPackageSampleClass())
    a.assertThat(underlyingSimpleTypeNameOf(asVal))
      .isEqualTo("TestCurrentPackageSampleClass")

    a.assertAll()

}


class TopClassOrModuleFullNameTest {
  // counter for compilation only
  val number = 19

  @Test
  def topClassOrModuleFullNameAsGlobalVar(): Unit =
    assertThat(topClassOrModuleFullNameGlobal).isEqualTo("org.mvv.scala.tools.quotes.BaseMacrosTest")

  @Test
  def topClassOrModuleFullNameInsideMethod(): Unit =
    val topClassOrModuleFullNameVal = topClassOrModuleFullName
    assertThat(topClassOrModuleFullNameVal).isEqualTo("org.mvv.scala.tools.quotes.TopClassOrModuleFullNameTest")

  @Test
  def topClassOrModuleFullNameInClass(): Unit =
    assertThat(TestCurrentPackageSampleClass().topClassOrModuleFullNameVal).isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleClass")

  @Test
  def topClassOrModuleFullNameInObject(): Unit =
    assertThat(TestCurrentPackageSampleObject.topClassOrModuleFullNameVal).isEqualTo("org.mvv.scala.tools.quotes.TestCurrentPackageSampleObject")

}
