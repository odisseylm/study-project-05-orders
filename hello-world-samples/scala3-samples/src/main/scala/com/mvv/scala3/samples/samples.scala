//noinspection ScalaUnusedSymbol
package com.mvv.scala3.samples

trait Trait1 {
  val trait1Val: String = ""
  var trait1Var: String = ""
  def trait1ValMethod: String = ""
  def trait1Method(): String = ""
}


trait Trait2 {
  val trait2Val: String = ""
  var trait2Var: String = ""
  def trait2ValMethod: String = ""
  def trait2Method(): String = ""
}


class BaseClass1 {
  private val privateValField0: String = ""
  protected val protectedValField0: String = ""
  val publicValField0: String = ""

  private var privateVarField0: String = ""
  protected var protectedVarField0: String = ""
  var publicVarField0: String = ""

  private def privateMethod0: String = ""

  protected def protectedMethod0: String = ""

  def publicMethod0: String = ""


  private val privateValField1: String = ""
  protected val protectedValField1: String = ""
  val publicValField1: String = ""

  private var privateVarField1: String = ""
  protected var protectedVarField1: String = ""
  var publicVarField1: String = ""

  private def privateMethod1: String = ""

  protected def protectedMethod1: String = ""

  def publicMethod1: String = ""
}


class InheritedClass1 extends BaseClass1 with Trait1 with Trait2 {
  private val privateValField1: String = ""
  protected override val protectedValField1: String = ""
  override val publicValField1: String = ""

  private var privateVarField1: String = ""
  // override protected var protectedVarField1: String = "" // impossible override protected mutable var
  // override var publicVarField1: String = "" // impossible override protected mutable var
  // /*override*/ def publicVarField1: String = ""
  // /*override*/ def publicVarField1_= (v: String): Unit = ""

  private def privateMethod1: String = ""

  protected override def protectedMethod1: String = ""

  override def publicMethod1: String = ""

  private val privateValField2: String = ""
  protected val protectedValField2: String = ""
  val publicValField2: String = ""

  private var privateVarField2: String = ""
  protected var protectedVarField2: String = ""
  var publicVarField2: String = ""

  private def privateMethod2: String = ""
  protected def protectedMethod2: String = ""
  def publicMethod2: String = ""
}


class InheritedFromJavaClass1 extends BaseJavaClass2 with Trait1 with Trait2 {
  private val privateValField1: String = ""
  protected val protectedValField1: String = ""
  val publicValField1: String = ""

  private def privateMethod2: String = ""
  protected def protectedMethod2: String = ""
  def publicMethod2: String = ""
}


class InheritedFromJavaClass2 extends BaseJavaClass1 with Trait1 with Trait2 with JavaInterface2 with JavaInterface1 {
  private val privateValField1: String = ""
  protected val protectedValField1: String = ""
  val publicValField1: String = ""

  private var privateVarField1: String = ""
  protected var protectedVarField1: String = ""
  var publicVarField1: String = ""

  private def privateValMethod2: String = ""
  protected def protectedValMethod2: String = ""
  def publicValMethod2: String = ""

  private def privateMethod2(): String = ""
  protected def protectedMethod2(): String = ""
  def publicMethod2(): String = ""
}
