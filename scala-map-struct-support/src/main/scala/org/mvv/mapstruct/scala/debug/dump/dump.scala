package org.mvv.mapstruct.scala.debug.dump

import org.mvv.mapstruct.scala.Logger
import scala.quoted.{Expr, Quotes, Type}


val log: Logger = Logger("org.mvv.mapstruct.scala.debug.dump")

inline def dumpExpr[T](inline expr: T): T =
  ${ dumpExprImpl('expr) }


private def dumpExprImpl[T](expr: Expr[T])(using quotes: Quotes)(using Type[T]): Expr[T] =
  import quotes.reflect.*
  val asTerm = expr.asTerm

  log.info(s"dumpExpr => expr [$expr], [${expr.show}], as term [$asTerm].")
  dumpTree1(asTerm)

  expr


