package org.mvv.scala.tools.quotes

import scala.annotation.targetName
import scala.quoted.*
import scala.reflect.ClassTag
import scala.reflect.Manifest
import scala.reflect.ClassManifest
//
import org.mvv.scala.tools.{ Logger, lastAfter, isImplClass, isOneOfImplClasses }


// TODO: how to ge current package name
//private val log: Logger = Logger("org.mvv.scala.quotes.quotesTypes")


/*
Existential types are no longer supported - use a wildcard or dependent type instead
inline def toQuotesTreeType (using q: Quotes)
  (inline v: QTreeType forSome { type QTreeType <: q.reflect.Tree } )
  : Option[T] forSome { type T <: q.reflect.Tree } =
  ${ toQuotesTreeTypeImp?? l[T]('v) }
*/


extension (using q: Quotes) (inline el: q.reflect.Tree)
  inline def toQuotesTypeOf[T <: q.reflect.Tree]: Option[T] = toQuotesTreeType[q.reflect.Tree, T](el)
  inline def isQuotesTypeOf[T <: q.reflect.Tree]: Boolean   = toQuotesTreeType[q.reflect.Tree, T](el).isDefined

  /*
  ?? Seems it does not work ?? => Ambiguous overload. The overloaded alternatives
  @@specialized does not help in this case
  // example of overriding
  @targetName("toQuotesTypeOfApply")
  inline def toQuotesTypeOf[T <: q.reflect.Apply]: Option[T] =
    if isApply(el) then Option(el.asInstanceOf[T]) else None
  @targetName("isQuotesTypeOfApply")
  inline def isQuotesTypeOf[T <: q.reflect.Apply]: Boolean = isApply(el)
  */

// example of overriding
private inline def isApply(using q: Quotes)(el: q.reflect.Tree): Boolean =
  el.isOneOfImplClasses("Apply", "Apply345")


// example of overriding
//extension (using q: Quotes) (inline el: q.reflect.Tree)
//  @targetName("toQuotesTypeOfApply")
//  inline def toQuotesTypeOf[T <: q.reflect.Apply]: Option[T] =
//    if el.isOneOfImplClasses("Apply", "Apply345") then el.asInstanceOf[q.reflect.Apply] else None
//  @targetName("isQuotesTypeOfApply")
//  inline def isQuotesTypeOf[T <: q.reflect.Apply]: Boolean = el.toQuotesTypeOf[T].isDefined



// scala does not allow using (v: q.reflect.Tree) directly in macros inline redirection function
//inline def eee_toQuotesTreeType[T] (using q: Quotes) (inline v: q.reflect.Tree): Option[T] = _toQuotesTreeType[q.reflect.Tree, T](v)

//noinspection ScalaUnusedSymbol
// scala does not allow using (v: q.reflect.Tree) directly in macros inline redirection function
private inline def toQuotesTreeType[V, T] (inline el: V): Option[T] =
  ${ castToTypeImpl[V,T]('el, '{ true }) }


/**
 * It generates the following scala code
 *   if isQuotesType(tree, StringLiteral(typeName))
 *     then Option(tree.asInstanceOf[type])
 *     else None
 *
 * For example
 *   if isQuotesType(tree, StringLiteral("quotes.reflect.Apply"))
 *     then Option(tree.asInstanceOf[quotes.reflect.Apply])
 *     else None
 *
 * @param validateCompatibilityWithVAndTExpr  Now scala does not allow using path-dependent types in generics type
 *                                            for usual functions because generic type parameter is defined before
 *                                            any declarations of implicits/usings (partially possible for extension function) and
 *                                            alternative solution as 'Existential types' is already are not supported
 *                                            by scala3. Fot that reason validation can be done programmatically
 *                                            (during macros expansion). ??Probably it always should be true??
 */
private def castToTypeImpl[V, T](using q: Quotes)(using Type[V], Type[T])
                                (elExpr: Expr[V], validateCompatibilityWithVAndTExpr: Expr[Boolean]): Expr[Option[T]] =
  import q.reflect.*

  // it must be constant
  val validateCompatibilityWithVAndT: Boolean = validateCompatibilityWithVAndTExpr.valueOrAbort
  if validateCompatibilityWithVAndT then qValidateCastingFromTo[V, T]()

  val vTerm: Term = elExpr.asTerm

  val condIsQuotesType = applyIsQuotesType(vTerm, Type.show[T])
  val _applyOption = qOption[T](qInstanceOf[T](vTerm))
  val _noneOption = qOptionNone

  val _if = If(condIsQuotesType, _applyOption, _noneOption)

  val resExpr = _if.asExprOf[Option[T]]
  resExpr



// Apply(
//   Select(Select(Select(Select(Select(Ident(org),mvv),scala),quotes),quotesTypesStuff$package),isQuotesType),
//   List(Ident(tree),
//   Literal(Constant(Apply))))
// )
def applyIsQuotesType(using q: Quotes)(el: q.reflect.Term, quoteTypeName: String): q.reflect.Term =
  import q.reflect.*
  // TODO: try to find and call/use specified method isQuotesXXX (for example isQuotesApply)
  // if it exists (it will allow to override behavior for some quotes tags)
  //
  val funTerm = qFunction
    ("org.mvv.scala.tools.quotes.quotesTypesStuff$package.isQuotesTypeByName")
    ( Param("el", TypeRepr.of[Any]), Param("typeName", TypeRepr.of[String]) )
    ( TypeRepr.of[Boolean] )
  val apply = Apply(funTerm, List(el, Literal(StringConstant(quoteTypeName))))
  apply
