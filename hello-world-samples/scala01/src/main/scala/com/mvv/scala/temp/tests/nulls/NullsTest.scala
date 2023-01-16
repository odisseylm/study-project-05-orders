package com.mvv.scala.temp.tests.nulls

import scala.language.strictEquality
//import scala.language.
//import scala.language.unsafeNulls


case class Person(var username: String, var password: String)
case class Student(username: String, nick: String|Null)

class Student2 :
  var username: String = _


@main
def aaa2(): Unit = {

  val p1 = Person("John", "psw1")
  println(p1)
  //val p2 = Person("John", null)
  //println(p2)

  val s1 = Student("Cheburan", null)
  println(s1)

  val s2 = Student2()
  println(s2)

  val str1: String|Null = null
  println(str1)
}
