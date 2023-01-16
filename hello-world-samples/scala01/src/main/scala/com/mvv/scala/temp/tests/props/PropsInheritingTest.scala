package com.mvv.scala.temp.tests.props


abstract class Base1 :
  var prop1: String

class Derivs11 extends Base1() :
  var prop1: String = ""

class Derivs12 extends Base1() :
  private var _prop1: String = ""
  def prop1: String = _prop1
  def prop1_= (prop1: String): Unit = { this._prop1 = prop1 }

/*
class Derivs3 extends Base1() :
  private var _prop1: String = ""
  def prop1: String = _prop1
  private def prop1_= (prop1: String): Unit = { this._prop1 = prop1 }
*/

abstract class Base2 :
  val prop1: String

class Derivs21 extends Base2() :
  val prop1: String = ""

//class Derivs22 extends Base2() :
//  private val _prop1: String = ""    // !!! It does NOT work !!!
//  final def prop1: String = _prop1


abstract class Base3 :
  def bar: String


class Derivs31 extends Base3 :
  override val bar = "baz"    // !!! It DOES work !!!

