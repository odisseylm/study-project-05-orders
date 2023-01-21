package com.mvv.scala.temp.tests.macros

import scala.quoted.{Quotes, *}


def a1(): Unit = {}
def a2(): Unit = {}

/*
def createFunctionBody(param: Term): Term = ...

def createFunction[A: Type]: Expr[A => String] =
  '{(a: A) => ${createFunctionBody('{a}.asTerm)}}

val myMethodSymbol = Symbol.newMethod(
  Symbol.spliceOwner,
  "myMethod",
  MethodType(
    List("param"))( // parameter list - here a single parameter
    _ => List(typeReprOfParam), // type of the parameter - here dynamic, given as a TypeRepr
    _ => TypeRepr.of[Int])) // return type - here static, always Int

// tree representing: def myMethod(param) = ...
val myMethodDef = DefDef(
  myMethodSymbol, {
    case List(List(paramTerm: Term)) => Some(myMethodBody(paramTerm).changeOwner(myMethodSymbol))
  }
)

// the myMethodDef can be now spliced in a larger expression or e.g. used in a Block(...)

*/

/*
import scala.quoted.*
import quotes.reflect.*



val myMethodSymbol = Symbol.newMethod(
  Symbol.spliceOwner,
  "myMethod",
  MethodType(
    List("param"))( // parameter list - here a single parameter
    _ => List(typeReprOfParam), // type of the parameter - here dynamic, given as a TypeRepr
    _ => TypeRepr.of[Int])) // return type - here static, always Int

// tree representing: def myMethod(param) = ...
val myMethodDef = DefDef(
  myMethodSymbol, {
    case List(List(paramTerm: Term)) => Some(myMethodBody(paramTerm).changeOwner(myMethodSymbol))
  }
)

inline def test555[T]: Unit = ${test555Impl[T]}

trait MyTypeclass[T]
given MyTypeclass[String] with {}

def myMethod[T](using MyTypeclass[T]): Unit = ()


def test555Impl[T: Type](using Quotes): Expr[Unit] = {
  import quotes.reflect.*
  '{myMethod[T]} // (1)
  // or:
  '{myMethod[T](${Expr.summon[MyTypeclass[T]].get})} // (2)
}

*/

/*
def aaa()(using quotes: Quotes): Unit = {

  val symbol: Symbol = ???
  //val quotes: quoted.Quotes = ???
  //val symbolModule: quoted.Quotes.reflectModule.SymbolModule = ???
  //quotes.reflect.Tree.valueOrAbort()

  val parent: quotes.reflect.Symbol = ???

  val flags: quotes.reflect.Flags =

  quotes.reflect.Symbol.newVal(parent, "dfdf", )

  //quotes.valueOrAbort
  //symbolModule.

}
*/


inline def addOneXv2(inline x: Int): Int = ${addOneXv2Impl('{x})}

// TypeRepr.of[F[A]]
//
def addOneXv2Impl(x: Expr[Int])(using Quotes): Expr[Int] =
  import quotes.reflect.*
  val rhs = Expr(x.valueOrAbort + 1)
  val sym = Symbol.newVal(
    Symbol.spliceOwner,
    "x",
    TypeRepr.of[Int],
    //Flags.EmptyFlags,
    //Flags.Given,
    Flags.JavaStatic,
    Symbol.noSymbol,
  )
  val vd = ValDef(sym, Some(rhs.asTerm))
  Block(
    List(vd),
    Ref(sym)
  ).asExprOf[Int]



inline def addOneXv3(inline x: Int): Int = ${addOneXv3Impl('{x})}

// TypeRepr.of[F[A]]
def addOneXv3Impl(x: Expr[Int])(using Quotes): Expr[Int] =
  import quotes.reflect.*
  val rhs = Expr(x.valueOrAbort + 1)
  val sym = Symbol.newVal(
    Symbol.spliceOwner,
    "x",
    TypeRepr.of[Int],
    //Flags.EmptyFlags,
    //Flags.Given,
    Flags.JavaStatic,
    Symbol.noSymbol,
  )
  val vd = ValDef(sym, Some(rhs.asTerm))
  Block(
    List(vd),
    Ref(sym)
  ).asExprOf[Int]

