package org.mvv.scala.tools.props.subpackage1

import scala.compiletime.uninitialized
//
import org.junit.jupiter.api.{ Test, DisplayName }
import org.assertj.core.api.Assertions.assertThatCode
//
import org.mvv.scala.tools.isNotNull
import org.mvv.scala.tools.quotes.topClassOrModuleFullName
import org.mvv.scala.tools.props.{ currentClassIsInitializedProps, validateUninitializedProps }



class ClassWithLateInitPropsShort2 :
  var currency: String = uninitialized

  private val allIsInitializedProps: List[(String, () => Boolean)] = currentClassIsInitializedProps

  def validateLateInitProps(): Unit =
    validateUninitializedProps(topClassOrModuleFullName, allIsInitializedProps)

  // It is not supported now to use class methods
  // (only static/companion functions are supported)
  //private def isInitialized(v: AnyRef): Boolean = v.isNotNull


object ClassWithLateInitPropsShort2 :
  val v = 1
  private def isInitialized(v: AnyRef): Boolean = v.isNotNull



class LateInitWithCompanionPropsTest {
  import scala.language.unsafeNulls


  @Test
  @DisplayName("validateLateInitProps")
  def validateLateInitPropsTest(): Unit = {
    val v = ClassWithLateInitPropsShort2()

    assertThatCode { () => v.validateLateInitProps() }
      .hasMessage("The following props in org.mvv.scala.tools.props.subpackage1.ClassWithLateInitPropsShort2 are not initialized [currency].")

    v.currency = "EUR"
    assertThatCode( () => v.validateLateInitProps() ).doesNotThrowAnyException()
  }
}
