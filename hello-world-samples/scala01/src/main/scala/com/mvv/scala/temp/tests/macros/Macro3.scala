package com.mvv.scala.temp.tests.macros

import scala.annotation.unused


// anywhere
inline def myMacro(@unused inline param1: String): Int = ${MyMacro.myMacroImpl('param1)}

// possibly elsewhere
import scala.quoted.*

object MyMacro {
  def myMacroImpl(param1: Expr[String])(using Quotes): Expr[Int] = {
    import quotes.reflect.*
    '{42}
  }
}

// macro
object PrintTree {
  inline def printTree[T](inline x: T): Unit = ${printTreeImpl('x)}
  private def printTreeImpl[T: Type](x: Expr[T])(using qctx: Quotes): Expr[Unit] =
    import qctx.reflect.*
    println(x.asTerm.show(using Printer.TreeStructure))
    '{()}
}
