package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.Enum as ScalaEnum
//
import org.mvv.scala.quotes.{ extractTuple2EntryFromExpr, extractTuple2EntriesFromSeqExpr }
import org.mvv.scala.mapstruct.{ Logger, lastAfter }
// for debug only


//private val log: Logger = Logger("org.mvv.scala.mapstruct.mappers.macroParamsExtractors")


def extractCustomEnumMappingTuplesExpr[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using q: Quotes)(using Type[EnumFrom], Type[EnumTo])
  (inlinedExpr: Expr[Seq[(EnumFrom, EnumTo)]])
  : List[(String, String)] =

  import q.reflect.{ Inlined, asTerm }
  extractTuple2EntriesFromSeqExpr[EnumFrom, EnumTo, String, String]( inlinedExpr, enumMappingTuple2Extractor )



def extractCustomEnumMappingTupleExpr[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using q: Quotes)(using Type[EnumFrom], Type[EnumTo])
  (inlinedExpr: Expr[(EnumFrom, EnumTo)])
  : (String, String) =

  import q.reflect.{ Inlined, asTerm }
  extractTuple2EntryFromExpr[EnumFrom, EnumTo, String, String]( inlinedExpr, enumMappingTuple2Extractor )



private def enumMappingTuple2Extractor[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum](using q: Quotes)
  ( tupleTerms: (q.reflect.Tree, q.reflect.Tree) ): (String, String) =
  val enumFromValueTerm = tupleTerms._1
  val enumToValueTerm   = tupleTerms._2
  (extractSimpleName(enumFromValueTerm), extractSimpleName(enumToValueTerm))



// Select(Ident(TestEnum1),TestEnumValue4)
// Select(Select(Select(Select(Select(Select(Select(Ident(com),mvv),scala),temp),tests),macros2),TestEnum1)
//
private def extractSimpleName(using quotes: Quotes)(tree: quotes.reflect.Tree): String =
  import quotes.reflect.*
  val rawName: String = tree.symbol.name
  rawName.lastAfter('.').getOrElse(rawName)
