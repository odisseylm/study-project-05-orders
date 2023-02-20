package org.mvv.scala.tools.quotes

import scala.annotation.targetName
import scala.quoted.*
import scala.reflect.ClassTag
import scala.reflect.Manifest
import scala.reflect.ClassManifest
//
import org.mvv.scala.tools.Logger
import org.mvv.scala.tools.lastAfter
import org.mvv.scala.tools.isImplClass


// TODO: how to ge current package name
//private val log: Logger = Logger("org.mvv.scala.quotes.quotesTypes")


/*
Existential types are no longer supported - use a wildcard or dependent type instead
inline def toQuotesTreeType (using q: Quotes)
  (inline v: QTreeType forSome { type QTreeType <: q.reflect.Tree } )
  : Option[T] forSome { type T <: q.reflect.Tree } =
  ${ toQuotesTreeTypeImpl[T]('v) }
*/


extension (using q: Quotes) (inline el: q.reflect.Tree)
  inline def toQuotesTypeOf[T <: q.reflect.Tree]: Option[T] = toQuotesTreeType[q.reflect.Tree, T](el)
  inline def isQuotesTypeOf[T <: q.reflect.Tree]: Boolean = el.toQuotesTypeOf[T].isDefined


// scala does not allow using (v: q.reflect.Tree) directly in macros inline redirection function
//inline def eee_toQuotesTreeType[T] (using q: Quotes) (inline v: q.reflect.Tree): Option[T] = _toQuotesTreeType[q.reflect.Tree, T](v)

//noinspection ScalaUnusedSymbol
// scala does not allow using (v: q.reflect.Tree) directly in macros inline redirection function
private inline def toQuotesTreeType[V, T] (inline el: V): Option[T] =
  ${ castToTypeImpl[V,T]('el, '{ true }) }


/**
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

  // should be generated
  // if isQuotesType(tree, StringLiteral(typeName))
  //   then Option(tree.asInstanceOf[quotes.reflect.Apply])
  //   else None

  //val logPrefix = s"castToTypeImpl [ ${TypeRepr.of[V]} => ${TypeRepr.of[T]} ] "
  //log.trace(s"$logPrefix") // ?throws compilation error probably because 'log' is not created yet?

  // it must be constant
  val validateCompatibilityWithVAndT: Boolean = validateCompatibilityWithVAndTExpr.valueOrAbort
  if validateCompatibilityWithVAndT then qValidateCastingFromTo[V, T]()

  val vTerm: Term = elExpr.asTerm

  //val srcInlined: Inlined = v.asTerm.asInstanceOf[Inlined]
  //val asInstanceOfMethod = Symbol.newMethod(Symbol.noSymbol, "asInstanceOf", TypeRepr.of[T])
  //val fun = Select(srcInlined.body, asInstanceOfMethod)
  //val typeApply = TypeApply(fun, List(TypeTree.of[T]))
  //val resInlined = Inlined(srcInlined.call, srcInlined.bindings, typeApply)
  //resInlined.asExprOf[T]

  val condIsQuotesType = applyIsQuotesType(vTerm, Type.show[T])
  val _applyOption = qOption[T](qInstanceOf[T](vTerm))
  val _noneOption = qOptionNone

  val _if = If(condIsQuotesType, _applyOption, _noneOption)

  val resExpr = _if.asExprOf[Option[T]]
  println(s"resExpr: ${resExpr.show}")
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
  // to call/apply function org.mvv.scala.quotes.isQuotesType
  val rootPackageIdent = Ident(Symbol.requiredPackage("org").termRef)
  // TODO: try to use qPackage
  val funPackage = Select.unique(Select.unique(Select.unique(Select.unique(Select.unique(rootPackageIdent, "mvv"), "scala"), "tools"), "quotes"),
    "quotesTypesStuff$package")
  val methodType = MethodType(List("el", "typeName"))
    (_ => List[TypeRepr](TypeRepr.of[Any], TypeRepr.of[String]), _ => TypeRepr.of[Boolean])
  val fun = Symbol.newMethod(Symbol.noSymbol, "isQuotesTypeByName", methodType)
  val funSelect = Select(funPackage, fun)
  val apply = Apply(funSelect, List(el, Literal(StringConstant(quoteTypeName))))
  apply
