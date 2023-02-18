package org.mvv.scala.mapstruct.mappers

import scala.quoted.Quotes
//
import org.mvv.scala.mapstruct.Logger



private val log = Logger("org.mvv.scala.mapstruct.mappers.classUtils")

def findClassThisScopeTypeRepr(using quotes: Quotes)(symbol: quotes.reflect.Symbol): Option[quotes.reflect.TypeRepr] =
  import quotes.reflect.*

  val typeRepr : Option[TypeRepr] = symbol match
    case td if td.isTypeDef || td.isClassDef =>
      val thisTypeRepr: TypeRepr = TypeRef.unapply(symbol.typeRef)._1 // both work ok
      //val thisTypeRepr: TypeRepr = TermRef.unapply(symbol.termRef)._1 // both work ok
      Option(thisTypeRepr)

    case other =>
      log.warn(s"findCurrentScopeTypeRepr: Unexpected flow $other.")
      None

  typeRepr



// returns (or should return) className (without generics types)
def getTypeApplyClassName(using quotes: Quotes)(typeApply: quotes.reflect.TypeApply): String =
  import quotes.reflect.*

  val typeApplyFun: Term = typeApply.fun
  if typeApplyFun.isSelect then
    getTypeApplyClassNameBySelect(typeApplyFun.asInstanceOf[Select])
  else
    val tpe: TypeRepr = typeApply.tpe
    val resultingClassName: String = tpe.show
    resultingClassName

// returns (or should return) className (without generics types)
def getTypeApplyClassNameBySelect(using quotes: Quotes)(typeApply: quotes.reflect.Select): String =
  import quotes.reflect.TypeRepr
  val tpe: TypeRepr = typeApply.tpe
  val qualifierTpe: TypeRepr = typeApply.qualifier.tpe
  val resultingTpe = if tpe.classSymbol.isDefined then tpe else qualifierTpe
  val resultingClassName = resultingTpe.show
  resultingClassName
