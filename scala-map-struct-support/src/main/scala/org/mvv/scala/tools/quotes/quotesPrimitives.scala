package org.mvv.scala.tools.quotes

import scala.quoted.*
//
import org.mvv.scala.tools.afterLast



enum QResult :
  case AsIs, AsInlined



def qStringLiteral(using q: Quotes)(value: => String, qResult: QResult = QResult.AsIs): q.reflect.Term =
  import q.reflect.{ Literal, StringConstant }
  val resTerm = wrapQResult(Literal(StringConstant(value)), qResult)
  resTerm


//noinspection ScalaUnusedSymbol
def qStringLiteralExpr(using Quotes)(value: => String): Expr[String] =
  qStringLiteral(value, QResult.AsInlined).asExprOf[String]



def qInstanceOf[T](using q: Quotes)(using Type[T])(expr: q.reflect.Term): q.reflect.Term =
  import q.reflect.{ Symbol, TypeTree, Select, TypeRepr, TypeApply }
  val asInstanceOfMethod = Symbol.newMethod(Symbol.noSymbol, "asInstanceOf", TypeRepr.of[T])
  val fun = Select(expr, asInstanceOfMethod)
  val typeApply = TypeApply(fun, List(TypeTree.of[T]))
  typeApply



def wrapQResult(using q: Quotes)(v: q.reflect.Term, qResult: QResult): q.reflect.Term = qResult match
  case QResult.AsInlined => q.reflect.Inlined(None, Nil, v)
  case QResult.AsIs => v



// [scala.Option.apply[java.lang.String](v)], as term
// Apply(
//    TypeApply(
//      Select( Ident(Option), apply ),
//      List( TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class lang)),class String)] )
//    ),
//    List( Ident(v) )
//  )
//
def qOption[T](using q: Quotes)(using Type[T])(expr: q.reflect.Term): q.reflect.Term =
  import q.reflect.{ Apply, Select, TypeApply, TypeTree }

  val optionApplySelect = Select.unique(Select.unique(qScalaPackage, "Option"), "apply")
  val typeApply = TypeApply(optionApplySelect, List(TypeTree.of[T]))
  Apply(typeApply, List(expr))



def qOptionNone(using q: Quotes): q.reflect.Term =
  q.reflect.Select.unique(qScalaPackage, "None")



//noinspection NoTailRecursionAnnotation, ScalaUnusedSymbol // there is no recusrsion at all,ScalaUnusedSymbol
def qValidateCastingFromTo[From, To](using Quotes, Type[From], Type[To])(): Unit =
  qValidateCastingFromTo[From, To](true)

def qValidateCastingFromTo[From, To](using q: Quotes)(using Type[From], Type[To])(abort: Boolean): Unit =
  import q.reflect.{ TypeRepr, report }
  val vTypeRepr = TypeRepr.of[From]
  val tTypeRepr = TypeRepr.of[To]
  val typesAreCompatible = tTypeRepr <:< vTypeRepr
  if !typesAreCompatible then
    val errMsg = s"Type ${tTypeRepr.show} cannot be cast to ${vTypeRepr.show}."
    if abort then report.errorAndAbort(errMsg) else report.error(errMsg)
