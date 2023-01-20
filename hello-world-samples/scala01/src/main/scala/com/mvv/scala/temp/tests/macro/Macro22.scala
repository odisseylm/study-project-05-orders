package com.mvv.scala.temp.tests.`macro`

import scala.annotation.unused
import scala.quoted.*


// sample from https://dotty.epfl.ch/docs/reference/metaprogramming/macros.html#
inline def assert22(@unused inline expr: Boolean): Unit =
  ${ assert22Impl('expr) }

def assert22Impl(expr: Expr[Boolean])(using Quotes): Expr[?] = '{
  if !$expr then
    throw AssertionError(s"failed assertion: ${${ showExpr(expr) }}")
}

//noinspection ScalaUnusedSymbol
def showExpr[T](expr: Expr[T])(using Quotes): Expr[String] =
  val code: String = expr.show
  Expr(code)


// can be called inside function
inline def generateLocalCode1(@unused inline expr: String): Unit =
  ${ generateLocalCode1Impl('expr) }
  //${ generateLocalCode1Impl(Symbol("expr")) }

def generateLocalCode1Impl(@unused expr: Expr[String])(using Quotes): Expr[?] = '{
  // this code will be generated instead of call
  val str: String = $expr
  println(s"from macro: input string [$str]")
}
