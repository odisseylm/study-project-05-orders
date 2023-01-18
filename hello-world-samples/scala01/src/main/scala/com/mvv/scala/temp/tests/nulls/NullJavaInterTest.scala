package com.mvv.scala.temp.tests.nulls

import scala.compiletime.uninitialized

//import scala.language.unsafeNulls
//import scala.language.unsafeJavaReturn


def test55656(): Unit = {
  val ss = "  "

  val trimmed1: String|Null = ss.trim
  val trimmed2: String = ss.trim.nn

  val jObj1 = new JavaClass()

  jObj1.nullableMethod(null)
  jObj1.nullableMethod(trimmed1)
  jObj1.nullableMethod(trimmed2)
  jObj1.nullableMethod("666")
  //val aa11: String = jObj1.nullableMethod(null) // not compiled - good
  val aa12: String|Null = jObj1.nullableMethod(null)

  jObj1.nonNullMethod(null)     // TODO: Why it successfully compiled?
  jObj1.nonNullMethod(trimmed1) // Why it successfully compiled?
  jObj1.nonNullMethod(trimmed2)
  jObj1.nonNullMethod("666")
  val aa21: String|Null = jObj1.nonNullMethod(null)
  val aa22: String = jObj1.nonNullMethod(null)
}
