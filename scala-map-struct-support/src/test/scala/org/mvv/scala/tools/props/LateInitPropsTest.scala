package org.mvv.scala.tools.props

import scala.annotation.targetName
import scala.compiletime.uninitialized
//
import java.util.concurrent.atomic.AtomicReference
//
import org.junit.jupiter.api.{ Test, DisplayName }
import org.assertj.core.api.Assertions.assertThatCode
//
import org.mvv.scala.tools.Logger
import org.mvv.scala.tools.quotes.topClassOrModuleFullName



val log: Logger = Logger(topClassOrModuleFullName)

// marker interface
//trait LateInitMarkerInterface // T O D O: do we need it? How to use it

class SkipLateInitCheck extends scala.annotation.StaticAnnotation
class SkipUninitializedCheck extends scala.annotation.StaticAnnotation
class SkipInitializedCheck extends scala.annotation.StaticAnnotation

package lowercase {
  class skipLateInitCheck extends scala.annotation.StaticAnnotation
  class skipUninitializedCheck extends scala.annotation.StaticAnnotation
  class skipInitializedCheck extends scala.annotation.StaticAnnotation
}

case class CustomStringLateInit(var v: String|Null = null) : //extends LateInitMarkerInterface :
  def safeValue: String = v.nn


class ClassWithLateInitPropsShort :
  // standard scala late-init props
  var currency: String = uninitialized

  def validateLateInitProps(): Unit =
    //noinspection ScalaUnusedSymbol
    val vv = 4 // to enforce recompilation

    val allProps: List[(String, () => Boolean)] =
      currentClassIsInitializedProps("org.mvv.scala.tools.props.IsInitialized")
    validateUninitializedProps(topClassOrModuleFullName, allProps)


def validateUninitializedProps(className: String, isInitializedProps: List[(String, ()=>Boolean)]): Unit =

  val uninitializedProps: List[String] = isInitializedProps
    .filter { (_, isInitialized) => !isInitialized() }
    // remove '_' which is used for internal private field
    // when it is accessed with the method with the same name (but without '_')
    .map(_._1.stripPrefix("_"))
    .sorted

  if uninitializedProps.nonEmpty then
    throw IllegalStateException(s"The following props in $className are not initialized [${uninitializedProps.mkString(", ")}].")



class ClassWithLateInitProps :
  // to force recompilation
  val v = 3
  // standard scala late-init props
  var amount: BigDecimal = uninitialized
  var currency: String = uninitialized

  //var amountInitialized: BigDecimal = BigDecimal(1)
  //var currencyInitialized: String = "EUR"
  //var user: String = "user1"

  var optionProp1: Option[String] = Option("John")
  var optionProp2: Option[String] = None

  @SkipLateInitCheck
  var shouldBeSkippedProp1: String = uninitialized
  @SkipUninitializedCheck
  var shouldBeSkippedProp2: String = uninitialized
  @SkipInitializedCheck
  var shouldBeSkippedProp3: String = uninitialized

  @lowercase.skipLateInitCheck
  var shouldBeSkippedProp4: String = uninitialized
  @lowercase.skipUninitializedCheck
  var shouldBeSkippedProp5: String = uninitialized
  @lowercase.skipInitializedCheck
  var shouldBeSkippedProp6: String = uninitialized

  //noinspection VarCouldBeVal
  private var _label1 = CustomStringLateInit()
  def label: String = _label1.safeValue
  def label_= (v: String): Unit = _label1.v = v

  private val _label2 = AtomicReference[String]()
  def label2: String = _label2.get().nn
  def label2_= (v: String): Unit = _label2.set(v)

  private val _label3 = AtomicReference[String]()

  def label3: String = _label3.get().nn
  def label3_= (v: String): Unit = _label3.set(v)

  def validateLateInitProps(): Unit =
    //noinspection ScalaUnusedSymbol
    val vv = 3 // to enforce recompilation
    val allProps: List[(String, ()=>Boolean)] =
      currentClassIsInitializedProps(List("org.mvv.scala.tools.props.IsInitialized"), "isInitialized")

    log.info(s"allProps: ${allProps.map(_._1)}")
    allProps.foreach { a => log.info(s"validateLateInitProps => prop(${a._1}, initialized=${a._2()})") }

    val initializedProps = allProps.filter((_, isInitialized) => !isInitialized()).map(_._1)
    val uninitializedProps = allProps.filter((_, isInitialized) => !isInitialized()).map(_._1)

    log.info(s"initializedProps: $initializedProps")
    log.info(s"uninitializedProps: $uninitializedProps")

    validateUninitializedProps(topClassOrModuleFullName, allProps)


  //def isInitialized(v: AnyRef): Boolean =
  //  import scala.language.unsafeNulls
  //  v != null


object IsInitialized :
  def isInitialized(v: Any): Boolean =
    import scala.language.unsafeNulls
    log.info(s"isInitialized($v: Any)")
    true
  @targetName("isAnyRefInitialized")
  def isInitialized(v: AnyRef): Boolean =
    import scala.language.unsafeNulls
    log.info(s"isInitialized($v: AnyRef)")
    v != null
  def isInitialized(v: String): Boolean =
    import scala.language.unsafeNulls
    log.info(s"isInitialized($v: String)")
    v != null && v.nonEmpty
  def isInitialized(v: CustomStringLateInit): Boolean =
    log.info(s"isInitialized($v: CustomStringLateInit)")
    import scala.language.unsafeNulls
    v.v != null
  def isInitialized(v: Option[Any]): Boolean =
    log.info(s"isInitialized($v: Option[?])")
    v.isDefined



class LateInitPropsTest {
  import scala.language.unsafeNulls

  val val1: String = ""
  val val2: Int = 0

  /*
  def aaaaa(): Unit = {
    val fff: List[(String, ()=>Boolean)] =  org.mvv.scala.mapstruct.debug.dump.dumpExpr(
      List(
        ("val1", () => isInitialized(val1)),
        ("val2", () => isInitialized(val2)),
      )
    )
  }

  def aaa2(): Unit = {
    val fff: (String, ()=>Boolean) = org.mvv.scala.mapstruct.debug.dump.dumpExpr( ("1", ()=>true) )
  }
  */


  @Test
  @DisplayName("validateUninitializedProps")
  def validateUninitializedPropsTest(): Unit = {
    val v = ClassWithLateInitProps()

    assertThatCode { () => v.validateLateInitProps(); () }
      .hasMessage("The following props in org.mvv.scala.tools.props.ClassWithLateInitProps" +
        " are not initialized [amount, currency, label1, optionProp2].")
      .isExactlyInstanceOf(classOf[IllegalStateException])

    v.label = "0"
    v.amount = BigDecimal("1")
    v.currency = "EUR"
    v.optionProp2 = Option("3")

    assertThatCode { () => v.validateLateInitProps(); () }
      .doesNotThrowAnyException()
  }


  @Test
  @DisplayName("validateUninitializedProps2")
  def validateUninitializedPropsTest2(): Unit = {
    val v = ClassWithLateInitPropsShort()

    assertThatCode { () => v.validateLateInitProps(); () }
      .hasMessage("The following props in org.mvv.scala.tools.props.ClassWithLateInitPropsShort" +
        " are not initialized [currency].")
      .isExactlyInstanceOf(classOf[IllegalStateException])
  }

}
