package com.mvv.scala.temp.tests.abstracts

import scala.compiletime.uninitialized


trait Abstract1 :
  val stringVal: String

//noinspection ScalaUnusedSymbol
class Abstract1Impl0 extends Abstract1 :
  //override def stringVal: String = { "ggg" } // NOT allowed in scala
  override val stringVal: String = "fck"

//class Abstract1Impl1 extends Abstract1 :
//  def stringVal: String = { "ggg" } // NOT allowed in scala

//class Abstract1Impl2 extends Abstract1 :
//  override lazy val stringVal: String = { "fck" } // Using lazy NOT allowed in scala !!! mthfck. Why???


trait Abstract2 :
  var stringVar: String

class Abstract2Impl0 extends Abstract2 :
  /*override*/
  var stringVar: String = uninitialized // _ // !!! Strange - using 'override' is not allowed here

//class Abstract2Impl1 extends Abstract2 :
//  override val stringVal: String = "fck" // not allowed, it is logical

class Abstract2Impl2 extends Abstract2 :
  private var _stringVar: String = uninitialized // _
  // !!! Strange - using 'override' is not allowed
  /*override*/ def stringVar: String = this._stringVar
  /*override*/ def stringVar_= (stringVar: String): Unit = { this._stringVar = stringVar }


@main
def test(): Unit = {

  val a1: Abstract2 = Abstract2Impl0()
  a1.stringVar = "123"
  println(s"a1.stringVar: ${a1.stringVar}")

  val a2: Abstract2 = Abstract2Impl2()
  a2.stringVar = "123"
  println(s"a2.stringVar: ${a2.stringVar}")
}
