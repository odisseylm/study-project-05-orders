package com.mvv.scala.temp.tests.macros

import scala.quoted.*


def printlnUppercaseImpl(str: Expr[String])(using q: Quotes) : Expr[Unit] = {
  val expr: Expr[String] = '{ $str.toUpperCase.nn } // (1)
  '{ println($expr) } // (2)
}


inline def logAST[T](inline expression: T) = ${ logASTImpl('expression) }

def logASTImpl[T: Type](expression: Expr[T])(using q: Quotes) : Expr[T]= { // (3)
  import quotes.reflect.* // (4)
  val term = expression.asTerm // (5)
  // !!! it would be printed during compilation
  println(s"===========Tree of type ${Type.show}=========:") // (6)
  println()
  println(term.show(using Printer.TreeAnsiCode))  // (7)
  println()
  println(term.show(using Printer.TreeStructure)) // (8)
  println()
  println("===========================")
  expression // (9)
}


def printSymbolsImpl(using q: Quotes) : Expr[Function0[Unit]] = {
  import quotes.reflect.*

  val owner = Expr(Symbol.spliceOwner.name) // (1)
  val parent = Expr(Symbol.spliceOwner.owner.name)
  val grandParent = Expr(Symbol.spliceOwner.owner.owner.name)

  val functionBody: Expr[Unit] = '{ // (2)
    println(s"Splice owner: ${$owner}, parent ${$parent}, grandParent ${$grandParent}")
  }

  val functionDefSymbol = Symbol.newMethod( // (3)
    Symbol.spliceOwner,
    "printSymbolsGenerated",
    MethodType(Nil)(
      _ => Nil,
      _ => TypeRepr.of[Unit]
    )
  )

  val functionDef = DefDef(functionDefSymbol, {  // (4)
    case _ => Some(functionBody.asTerm.changeOwner(functionDefSymbol))
  })

  Block(List(functionDef), Closure(Ref(functionDefSymbol), None)).asExprOf[Function0[Unit]]  // (5)
}

