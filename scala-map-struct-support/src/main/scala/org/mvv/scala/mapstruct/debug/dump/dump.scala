package org.mvv.scala.mapstruct.debug.dump

import scala.quoted.{Expr, Quotes, Type}
//
import org.mvv.scala.tools.Logger


private val log: Logger = Logger("org.mvv.scala.mapstruct.debug.dump")


//noinspection ScalaUnusedSymbol
inline def dumpExpr[T](inline expr: T): T =
  ${ dumpExprImpl('expr) }


//noinspection ScalaUnusedSymbol
private def dumpExprImpl[T](expr: Expr[T])(using quotes: Quotes)(using Type[T]): Expr[T] =
  import quotes.reflect.*
  val asTerm = expr.asTerm

  log.info(s"dumpExpr => expr [$expr], [${expr.show}], as term [$asTerm].")
  dumpTree1(asTerm)

  expr


