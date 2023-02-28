package org.mvv.scala.tools.props

import org.junit.jupiter.api.Test

import java.util.concurrent.atomic.AtomicReference
import scala.compiletime.uninitialized


// marker interface
trait LateInitMarkerInterface
// or as annotation
//case class lateInit(name: String) extends scala.annotation.StaticAnnotation
//@scala.annotation.meta.field
//@scala.annotation.meta.getter
//@scala.annotation.meta.setter
class LateInit34 extends scala.annotation.StaticAnnotation



case class CustomStringLateInit(var v: String|Null = null) extends LateInitMarkerInterface :
  def safeValue: String = v.nn


class ClassWithLateInitPropsShort :
  // standard scala late-init props
  var currencyNotInitialized: String = uninitialized

  def validateLateInitProps(): Unit =
    val vv = 2
    val _lateInitProps22: List[(String, () => Boolean)] = currentClassIsInitializedProps
    println(s"lateInitProps22: $_lateInitProps22")
    _lateInitProps22.foreach { a => println(s"^^^ ${a._1} initialized = ${a._2()}") }



class ClassWithLateInitProps :
  // standard scala late-init props
  var amountNotInitialized: BigDecimal = uninitialized
  var currencyNotInitialized: String = uninitialized

  var amountInitialized: BigDecimal = BigDecimal(1)
  var currencyInitialized: String = "EUR"

  var user: String = "user1"

  //noinspection VarCouldBeVal
  private var _label1 = CustomStringLateInit()
  def label: String = _label1.safeValue
  def label_= (v: String): Unit = _label1.v = v

  @LateInit33
  @LateInit34
  private val _label2 = AtomicReference[String]()
  def label2: String = _label2.get().nn
  def label2_= (v: String): Unit = _label2.set(v)

  private val _label3 = AtomicReference[String]()

  @LateInit33
  @LateInit34
  def label3: String = _label3.get().nn
  def label3_= (v: String): Unit = _label3.set(v)

  def validateLateInitProps(): Unit =
    val vv = 2
    val _lateInitProps22: List[(String, ()=>Boolean)] = currentClassIsInitializedProps
    println(s"lateInitProps22: $_lateInitProps22")

    _lateInitProps22.foreach { a => println(s"^^^ ${a._1} initialized = ${a._2()}") }
    return


  //def isInitialized(v: AnyRef): Boolean =
  //  import scala.language.unsafeNulls
  //  v != null


object IsInitialized :
  def isInitialized(v: AnyRef): Boolean =
    import scala.language.unsafeNulls
    v != null
  def isInitialized(v: String): Boolean =
    import scala.language.unsafeNulls
    v != null && v.nonEmpty
  def isInitialized(v: CustomStringLateInit): Boolean =
    import scala.language.unsafeNulls
    v.v != null
  def isInitialized(v: Option[Any]): Boolean =
    v.isDefined



class LateInitPropsTest {

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
  def aaa(): Unit = {
    org.mvv.scala.tools.beans.ScalaBeansInspector().inspectClass(classOf[ClassWithLateInitProps])
  }

  @Test
  def bb(): Unit = {
    val v = ClassWithLateInitProps()
    v.validateLateInitProps()
  }

}


