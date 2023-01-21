package com.mvv.implicits

import scala.language.implicitConversions


//noinspection ScalaUnusedSymbol,ScalaFileName
object ImplicitConversion :
  implicit def autoOption[T](x: T): Option[T] = Option(x)
