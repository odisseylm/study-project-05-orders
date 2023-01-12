package com.mvv.scala.temp.tests.typemixing


trait A
trait B

class AImpl extends A

class BImpl extends B

class ABImpl extends A, B

trait TypeMixing {
  val fieldOfMixedType1: String | Int
  val fieldOfMixedType2: String & Int
  val fieldOfMixedType3: A | B
  val fieldOfMixedType4: A & B
}

class TypeMixingClass {
  var fieldOfMixedType1: String | Int = _
  var fieldOfMixedType2: String & Int = _
  var fieldOfMixedType3: A | B = _
  var fieldOfMixedType4: A & B = _
}


def aa(): Unit = {
  var v: String | Int | Null = null
  println("fgd")
  v = 567

  val m = TypeMixingClass()
  m.fieldOfMixedType1 = 123
  m.fieldOfMixedType1 = "123"

  //m.fieldOfMixedType2 = "123"

  m.fieldOfMixedType3 = AImpl()
  m.fieldOfMixedType3 = BImpl()

  m.fieldOfMixedType4 = ABImpl()
  m.fieldOfMixedType3 = new BImpl with A

  val s: String = m.fieldOfMixedType1.asInstanceOf[String]
  val i: Int = m.fieldOfMixedType1.asInstanceOf[Int]
  val ddd: java.util.Date = m.fieldOfMixedType1.asInstanceOf[java.util.Date]
}
