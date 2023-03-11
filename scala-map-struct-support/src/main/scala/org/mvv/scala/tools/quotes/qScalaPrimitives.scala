package org.mvv.scala.tools.quotes

import scala.quoted.*
//
import org.mvv.scala.tools.afterLastOr



enum QResult derives CanEqual :
  case AsIs, AsInlined



def qStringLiteral(using q: Quotes)(value: => String, qResult: QResult = QResult.AsIs): q.reflect.Term =
  import q.reflect.{ Literal, StringConstant }
  //val resTerm = wrapQResult(Literal(StringConstant(value)), qResult)
  val constant = StringConstant(value)
  val literal = Literal(constant)
  val resTerm = wrapQResult(literal, qResult)
  resTerm



//noinspection ScalaUnusedSymbol
def qStringLiteralExpr(using Quotes)(value: => String): Expr[String] =
  qStringLiteral(value, QResult.AsInlined).asExprOf[String]



//noinspection ScalaUnusedSymbol
def qTuple2[T1,T2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (t1: q.reflect.Term, t2: q.reflect.Term): q.reflect.Term =
  import q.reflect.asTerm
  val tupleExpr: Expr[(T1, T2)] = Expr.ofTuple( ( t1.asExprOf[T1], t2.asExprOf[T2] ) )
  tupleExpr.asTerm



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



/*
/**
 * Use Expr.ofTuple instead of qTuple2 for creating tuple
 *
 * {{{
 *   val stringLiteral: Term = qStringLiteral(valDef.name)
 *   val funLambda: Term = isInitializedAnonFunLambda
 *
 *   val tupleExpr: Expr[(String, ()=>Boolean)] = Expr.ofTuple(
 *     (stringLiteral.asExprOf[String], funLambda.asExprOf[()=>Boolean]) )
 *   val tupleTerm = tupleExpr.asTerm
 * }}}
 */
def qTuple2(using q: Quotes)(v1: q.reflect.Term, v2: q.reflect.Term): q.reflect.Term =
  import q.reflect.*
  val tuple2ClassTerm: Term = qClassNameOfCompiled[Tuple2[Any,Any]]
  val tuple2ApplySelect = Select.unique(tuple2ClassTerm, "apply")
  val tuple2TypeApply = TypeApply(tuple2ApplySelect, List(TypeTree.of[String], TypeTree.of[()=>Boolean]))
  val tupleApply = Apply(tuple2TypeApply, List(v1, v2))
  tupleApply


def qStringValueTuple2(using q: Quotes)(str: String, value: q.reflect.Term): q.reflect.Term =
  qTuple2(qStringLiteral(str), value)
*/
