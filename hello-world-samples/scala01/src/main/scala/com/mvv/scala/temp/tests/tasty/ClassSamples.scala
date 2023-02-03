//noinspection ScalaUnusedSymbol ,  VarCouldBeVal
package com.mvv.scala.temp.tests.tasty


// Do NOT move it to test sources!
class ClassSampleInProductionSources :
  var var123: String = ""


case class CaseScalaClassSample (
    stringScalaValProp: String,
    optionalStringScalaValProp: Option[String],
  ) :
  val stringScalaVal: String = "VALUE_stringScalaVal"
  var stringScalaVar: String = "VALUE_stringScalaVar"

  private var _customStringScalaValProp: String = "VALUE_customStringScalaValProp"
  def customStringScalaValProp: String = _customStringScalaValProp

  private var _customStringScalaVarProp: String = "VALUE_customStringScalaValProp"
  def customStringScalaVarProp: String = _customStringScalaVarProp
  def customStringScalaVarProp_= (v: String): Unit = _customStringScalaVarProp = v

  def getterAction1Method: String = ""
  def action1Method(): String = ""

  private val privateVal: String = ""
  protected val protectedVal: String = ""
  val publicVal: String = ""

  private val privateMethod: String = ""
  protected val protectedMethod: String = ""
  val publicMethod: String = ""

end CaseScalaClassSample


class AccessVisibilityTestClass {
  private val privateVal: String = ""
  protected val protectedVal: String = ""
  val publicVal: String = ""

  private var privateVar: String = ""
  protected var protectedVar: String = ""
  var publicVar: String = ""

  private def privateMethod: String = ""
  protected def protectedMethod: String = ""
  def publicMethod: String = ""
}


class A1 :
  def method12345: String = ""
class A2 :
  def method12345(): String = ""
class A3 :
  def method12345(p1: Int, p2: String): String = ""

class UsualScalaClassSample (
    val stringScalaValProp: String,
    val optionalStringScalaValProp: Option[String],
    var stringScalaVar: String = "VALUE_stringScalaVar"
  ) :
  val stringScalaVal: String = "VALUE_stringScalaVal"
  //var stringScalaVar: String = "VALUE_stringScalaVar"

  private var _customStringScalaValProp: String = "VALUE_customStringScalaValProp"
  def customStringScalaValProp: String = _customStringScalaValProp

  private var _customStringScalaVarProp: String = "VALUE_customStringScalaVarProp"
  def customStringScalaVarProp: String = _customStringScalaVarProp
  def customStringScalaVarProp_= (v: String): Unit = _customStringScalaVarProp = v
end UsualScalaClassSample



trait Trait1 :
  val trait1Val: String = ""
  var trait1Var: String = ""
  def trait1ValMethod: String = ""
  def trait1Method(): String = ""

trait Trait2 :
  val trait2Val: String = ""
  var trait2Var: String = ""
  def trait2ValMethod: String = ""
  def trait2Method(): String = ""

class BaseClass1 :
  private val privateValField0: String = ""
  protected val protectedValField0: String = ""
  val publicValField0: String = ""

  private var privateVarField0: String = ""
  protected var protectedVarField0: String = ""
  var publicVarField0: String = ""

  private def privateValMethod0: String = ""
  protected def protectedValMethod0: String = ""
  def publicValMethod0: String = ""


  private val privateValField1: String = ""
  protected val protectedValField1: String = ""
  val publicValField1: String = ""

  private var privateVarField1: String = ""
  protected var protectedVarField1: String = ""
  var publicVarField1: String = ""

  private def privateMethod1(): String = ""
  protected def protectedMethod1(): String = ""
  def publicMethod1(): String = ""


// TODO: move to tests
class StandardScalaModifiersTesClass :
  private val privateValField1: String = ""
  protected val protectedValField1: String = ""
  val publicValField1: String = ""

  private var privateVarField1: String = ""
  protected var protectedVarField1: String = ""
  var publicVarField1: String = ""

  private def privateValMethod1: String = ""
  protected def protectedValMethod1: String = ""
  def publicValMethod1: String = ""

  private def privateMethod1(): String = ""
  protected def protectedMethod1(): String = ""
  def publicMethod1(): String = ""


class InheritedClass1 extends BaseClass1, Trait1, Trait2 :
  private val privateValField1: String = ""
  protected override val protectedValField1: String = ""
  override val publicValField1: String = ""

  private var privateVarField1: String = ""
  // override protected var protectedVarField1: String = "" // impossible override protected mutable var
  // override var publicVarField1: String = "" // impossible override protected mutable var
  // /*override*/ def publicVarField1: String = ""
  // /*override*/ def publicVarField1_= (v: String): Unit = ""

  private def privateMethod1: String = ""
  protected override def protectedMethod1(): String = ""
  override def publicMethod1(): String = ""

  private val privateValField2: String = ""
  protected val protectedValField2: String = ""
  val publicValField2: String = ""

  private var privateVarField2: String = ""
  protected var protectedVarField2: String = ""
  var publicVarField2: String = ""

  private def privateMethod2(): String = ""
  protected def protectedMethod2(): String = ""
  def publicMethod2(): String = ""


abstract class InheritedFromJavaClass1 extends BaseJavaClass2, Trait1, Trait2 :
  private val privateValField1: String = ""
  protected val protectedValField1: String = ""
  val publicValField1: String = ""

  private def privateMethod2: String = ""
  protected def protectedMethod2: String = ""
  def publicMethod2: String = ""


class InheritedFromJavaClass2 extends BaseJavaClass1, Trait1, Trait2, JavaInterface2, JavaInterface1 :
  private val privateValField1: String = ""
  protected val protectedValField1: String = ""
  val publicValField1: String = ""

  private def privateValMethod2: String = ""
  protected def protectedValMethod2: String = ""
  def publicValMethod2: String = ""

  private def privateMethod2(): String = ""
  protected def protectedMethod2(): String = ""
  def publicMethod2(): String = ""

  private var javaInterfaceValue11Var = ""
  override def getInterfaceValue11: String = javaInterfaceValue11Var
  override def setInterfaceValue11(v: String): Unit = { javaInterfaceValue11Var = v }
