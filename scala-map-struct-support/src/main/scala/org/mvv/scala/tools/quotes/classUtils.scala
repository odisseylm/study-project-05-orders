package org.mvv.scala.tools.quotes

import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ Logger, tryDo }



private val log = Logger(topClassOrModuleFullName)

def getClassThisScopeTypeRepr(using q: Quotes)(symbol: q.reflect.Symbol): q.reflect.TypeRepr =
  findClassThisScopeTypeRepr(symbol)
    .getOrElse {
      val sourceSymbolStr = tryDo { symbol.toString }
      throw IllegalStateException(s"Error of getting ClassThisScopeTypeRepr of $sourceSymbolStr.") }


def findClassThisScopeTypeRepr(using q: Quotes)(symbol: q.reflect.Symbol): Option[q.reflect.TypeRepr] =
  import q.reflect.{ TypeRepr, TypeRef }

  val typeRepr : Option[TypeRepr] = symbol match
    case td if td.isTypeDef || td.isClassDef =>
      val thisTypeRepr: TypeRepr = TypeRef.unapply(symbol.typeRef)._1 // both work ok
      //val thisTypeRepr: TypeRepr = TermRef.unapply(symbol.termRef)._1 // both work ok
      Option(thisTypeRepr)

    case other =>
      log.warn(s"findCurrentScopeTypeRepr: Unexpected flow $other.")
      None

  typeRepr



def findSpliceOwnerClass(using q: Quotes)(): Option[q.reflect.ClassDef] =
  import q.reflect.{ Symbol, ClassDef }

  var s: Symbol = Symbol.spliceOwner
  while s != Symbol.noSymbol && !s.isClassDef do
    s = s.maybeOwner

  if s.isClassDef then tryDo { s.tree match { case cd: ClassDef => cd } } else None



// returns (or should return) className (without generics types)
def getTypeApplyClassName(using q: Quotes)(typeApply: q.reflect.TypeApply): String =
  import q.reflect.Select

  val className = typeApply.fun match
    case typeApplySelect: Select => getTypeApplyClassNameBySelect(typeApplySelect)
    case _ => typeApply.tpe.show
  className



// returns (or should return) className (without generics types)
def getTypeApplyClassNameBySelect(using q: Quotes)(typeApply: q.reflect.Select): String =
  import q.reflect.TypeRepr
  val tpe: TypeRepr = typeApply.tpe
  val qualifierTpe: TypeRepr = typeApply.qualifier.tpe
  val resultingTpe = if tpe.classSymbol.isDefined then tpe else qualifierTpe
  val resultingClassName = resultingTpe.show
  resultingClassName
