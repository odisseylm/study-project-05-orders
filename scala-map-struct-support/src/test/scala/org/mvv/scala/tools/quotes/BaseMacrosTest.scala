package org.mvv.scala.tools.quotes

import scala.language.unsafeNulls
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat


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
