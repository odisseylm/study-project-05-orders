package org.mvv.scala.tools.props.subpackage1

import scala.compiletime.uninitialized
//
import org.mvv.scala.tools.isNotNull
import org.mvv.scala.tools.quotes.topClassOrModuleFullName
import org.mvv.scala.tools.props.{ currentClassIsInitializedProps, validateUninitializedProps }



class ClassWithLateInitPropsShort3 :
  var currency: String = uninitialized

  private val allIsInitializedProps: List[(String, () => Boolean)] = currentClassIsInitializedProps

  def validateLateInitProps(): Unit =
    validateUninitializedProps(topClassOrModuleFullName, allIsInitializedProps)

  // It is not supported now to use class methods
  // (only static/companion functions are supported)
  //private def isInitialized(v: AnyRef): Boolean = v.isNotNull

trait IsInitDefaults3 :
  def isInitialized(v: AnyRef): Boolean = v.isNotNull

object ClassWithLateInitPropsShort3 extends IsInitDefaults3 :
  val v = 1



/*
class LateInitWithCompanionWithInheritancePropsTest {
  import scala.language.unsafeNulls
  import org.junit.jupiter.api.{ Test, DisplayName }
  import org.assertj.core.api.Assertions.assertThatCode


  @Test
  @DisplayName("validateLateInitProps")
  def validateLateInitPropsTest(): Unit = {
    val v = ClassWithLateInitPropsShort3()

    assertThatCode { () => v.validateLateInitProps() }
      .hasMessage("The following props in org.mvv.scala.tools.props.subpackage1.ClassWithLateInitPropsShort3 are not initialized [currency].")

    v.currency = "EUR"
    assertThatCode( () => v.validateLateInitProps() ).doesNotThrowAnyException()
  }
}
*/
