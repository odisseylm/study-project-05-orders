package com.mvv.scala.temp.tests.tasty

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

  private val privateMethod: String = ""
  protected val protectedMethod: String = ""
  val publicMethod: String = ""
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
