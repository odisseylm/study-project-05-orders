package org.mvv.scala.tools.quotes

import scala.quoted.Quotes
//
import org.mvv.scala.tools.Logger



private val log = Logger("org.mvv.scala.tools.classUtils")

def findClassThisScopeTypeRepr(using q: Quotes)(symbol: q.reflect.Symbol): Option[q.reflect.TypeRepr] =
  import q.reflect.*

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
def getTypeApplyClassName(using q: Quotes)(typeApply: q.reflect.TypeApply): String =
  import q.reflect.*

  typeApply.fun.toQuotesTypeOf[Select]
    .map(typeApply => getTypeApplyClassNameBySelect(typeApply))
    .getOrElse {
      val tpe: TypeRepr = typeApply.tpe
      val resultingClassName: String = tpe.show
      resultingClassName
    }



// returns (or should return) className (without generics types)
def getTypeApplyClassNameBySelect(using q: Quotes)(typeApply: q.reflect.Select): String =
  import q.reflect.TypeRepr
  val tpe: TypeRepr = typeApply.tpe
  val qualifierTpe: TypeRepr = typeApply.qualifier.tpe
  val resultingTpe = if tpe.classSymbol.isDefined then tpe else qualifierTpe
  val resultingClassName = resultingTpe.show
  resultingClassName
