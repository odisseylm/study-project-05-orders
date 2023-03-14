package org.mvv.scala.tools.quotes

import scala.quoted.{ Expr, Quotes, Type }
import org.mvv.scala.tools.Logger



inline def dumpExpr[T](inline expr: T): T =
  ${ dumpExprImpl('{ expr }) }


private def dumpExprImpl[T](using q: Quotes)(using Type[T])(expr: Expr[T]): Expr[T] =
  import q.reflect.{ Printer, Term, asTerm }

  // or you can use as default
  // given Printer[Tree] = Printer.TreeStructure

  val log = Logger("dumpExpr")

  val term: Term = expr.asTerm

  log.info(s"${expr.show}")
  log.info(s"${term.show(using Printer.TreeCode)}")
  //log.info(s"${term.show(using Printer.TreeShortCode)}")
  //log.info(s"${term.show(using Printer.TreeAnsiCode)}")
  // structure of the AST
  log.info(s"${term.show(using Printer.TreeStructure)}")

  // types
  //log.info(s"${term.tpe.show(using Printer.TypeReprCode)}")
  //log.info(s"${term.tpe.show(using Printer.TypeReprShortCode)}")
  //log.info(s"${term.tpe.show(using Printer.TypeReprAnsiCode)}")
  //log.info(s"${term.tpe.show(using Printer.TypeReprStructure)}")

  // for printing constants
  //log.info(s"${term.show(using Printer.ConstantCode)}")
  //log.info(s"${term.show(using Printer.ConstantStructure)}")

  expr
