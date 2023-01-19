package com.mvv.scala.temp.tests.annotations

import scala.annotation.meta.{beanGetter, getter}
import scala.annotation.nowarn
import scala.compiletime.uninitialized


// @nowarn it does not work for method body warnings
def asd(): Unit = {
  val a = 123
  @nowarn
  val b = ""
}


class Abc {
  @(javax.annotation.Nonnull @beanGetter @getter)
  var aaa: String = uninitialized


}

//@BeanInfo
class ScalaBean1 {

  var prop1: String = uninitialized
}


//val strXml = xml""

//var x1: String = _ // Idea shows as error but it is compiled
var x2: String = uninitialized

class B :
  var x1: String = uninitialized
  var x2: String = uninitialized


