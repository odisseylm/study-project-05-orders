package com.mvv.scala.temp.tests.nulls

//import scala.language.unsafeNulls
//import scala.language.strictEquality


@main
def aaa(): Unit = {

  val s: String | Null = null //???

  if s != null && s.length > 0 then // s: String in `s.length > 0`
    println(s.length)

  if s == null || s.length > 0 then // s: String in `s.length > 0`
    ; //println(s.length)
  else
    println(s.length)
}

def foo(@javax.annotation.Nonnull a: Any): Unit = println(a.hashCode)
//def foo2(@jakarta.validation.NotNull a: Any) = println(a.hashCode)
