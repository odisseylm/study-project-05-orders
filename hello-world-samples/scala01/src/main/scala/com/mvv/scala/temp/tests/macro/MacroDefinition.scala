package com.mvv.scala.temp.tests.`macro`


/*
import scala.reflect.macros.Context
def assert(cond: Boolean, msg: Any) = macro Asserts.assertImpl

object Asserts {
  def raise(msg: Any) = throw new AssertionError(msg)
  def assertImpl(c: Context)
                (cond: c.Expr[Boolean], msg: c.Expr[Any]) : c.Expr[Unit] =
    if (assertionsEnabled)
      <[ if (!cond) raise(msg) ]>
    else
      <[ () ]>
}
*/

import scala.annotation.unused
import scala.compiletime.summonAll
import scala.deriving.Mirror
import scala.quoted.* // imports Quotes, Expr


inline def inspect(@unused inline x: Any): Any = ${ inspectCode('x) }
//inline def inspect33(@unused inline x: Any): Any = ${ inspectCode(Symbol("x")) }

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] =
  println(x.show)
  x




//inline def logged[T](inline x: T): T = ${ loggedCode('x)  }
//def loggedCode[T](x: Expr[T])(using Type[T], Quotes): Expr[T] = {  }


inline def test(@unused inline ignore: Boolean, @unused computation: => Unit): Boolean =
  ${ testCode('ignore, 'computation) }
  //${ testCode(Symbol("ignore"), Symbol("computation")) }

def testCode(ignore: Expr[Boolean], computation: Expr[Unit])(using Quotes) =
  if ignore.valueOrAbort then Expr(false)
  else Expr.block(List(computation), Expr(true))


//def newCode(ignore: Expr[String], computation: Expr[Unit])(using Quotes): scala.quoted.Expr[T] =
//def newCode234()(using Quotes): scala.quoted.Expr[String] =
//  //Exprs.asExprOf
//  //Expr.asExprOf[String]("val v345 = 1")
//  Expr.summon[String]("val v345 = 1")

//val msg = Expr("Hello")
//val printHello = '{ print($msg) }
//println(printHello.show) // print("Hello")


