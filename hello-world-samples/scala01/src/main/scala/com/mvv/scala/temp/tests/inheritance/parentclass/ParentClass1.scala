package com.mvv.scala.temp.tests.inheritance.parentclass

abstract class AbstractParentClass :
  var v: Int = 123

class StdParentClass :
  var v: Int = 123

open class OpenParentClass :
  var v: Int = 123

sealed class SealedParentClass :
  var v: Int = 123
