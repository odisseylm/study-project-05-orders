package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type, Varargs}
import scala.reflect.Enum as ScalaEnum
//
import org.mvv.scala.tools.quotes.extractEnumValueName
import org.mvv.scala.tools.{ Logger, afterLastOr, afterLastOfAnyCharsOr }



//noinspection ScalaUnusedSymbol
def extractCustomEnumMappingTuplesExpr[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using Quotes, Type[EnumFrom], Type[EnumTo])
  (tuplesSeqExpr: Expr[Seq[(EnumFrom, EnumTo)]])
  : List[(String, String)] =

  val asSeqOfExprs: Seq[Expr[(EnumFrom, EnumTo)]] = Varargs.unapply(tuplesSeqExpr).getOrElse(Nil)
  val tuplesWithName: List[(String, String)] = asSeqOfExprs
    .map(expr => extractCustomEnumMappingTupleExpr[EnumFrom, EnumTo](expr))
    .toList
  tuplesWithName



//noinspection ScalaUnusedSymbol
def extractCustomEnumMappingTupleExpr[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using Quotes, Type[EnumFrom], Type[EnumTo])
  (tupleExpr: Expr[(EnumFrom, EnumTo)])
  : (String, String) =
  val tupleOfExpr: (Expr[EnumFrom], Expr[EnumTo]) = tupleOfExprToExprOfTuple[EnumFrom, EnumTo](tupleExpr)
  val tuple: (String, String) = (extractEnumValueName[EnumFrom](tupleOfExpr._1), extractEnumValueName[EnumTo](tupleOfExpr._2))
  tuple



//noinspection ScalaUnusedSymbol
def tupleOfExprToExprOfTuple[EnumFrom, EnumTo]
  (using Quotes, Type[EnumFrom], Type[EnumTo])
  (exprOfTuple: Expr[(EnumFrom, EnumTo)])
  : (Expr[EnumFrom], Expr[EnumTo]) =
  exprOfTuple match
    case '{ ($x1: EnumFrom, $x2: EnumTo) } => (x1, x2)
