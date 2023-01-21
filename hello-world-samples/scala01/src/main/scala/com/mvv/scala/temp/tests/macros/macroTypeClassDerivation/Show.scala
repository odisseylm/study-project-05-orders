package com.mvv.scala.temp.tests.macros.macroTypeClassDerivation

trait Show[T]:
  def show(t: T): String
