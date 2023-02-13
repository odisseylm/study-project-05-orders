package org.mvv.mapstruct.scala.debug

import scala.quoted.{Expr, Quotes, Type}


inline def dumpExpr[T](inline expr: T): T =
  ${ dumpExprImpl('expr) }


private def dumpExprImpl[T](expr: Expr[T])(using quotes: Quotes)(using Type[T]): Expr[T] =
  import quotes.reflect.*
  val asTerm = expr.asTerm

  log.info(s"dumpExpr => expr [$expr], [${expr.show}], as term [$asTerm].")
  dumpTree33(asTerm)

  expr


