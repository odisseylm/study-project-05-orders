package org.mvv.scala.quotes

import scala.quoted.*
import scala.reflect.ClassTag
import scala.reflect.Manifest
import scala.reflect.ClassManifest
//
import org.mvv.scala.mapstruct.lastAfter
import org.mvv.scala.mapstruct.isImplClass


inline def isQuotesTypeOf
  //[T : ClassManifest]
  [T]
  (inline expr: Any)
  : Boolean =
  true


inline def toQuotesType22[T](inline v: Any): Option[T] =
  ${ toQuotesType22Impl[T]('v) }

private def toQuotesType22Impl[T](using q: Quotes)(using t: Type[T])(v: Expr[Any]): Expr[Option[T]] =
  import q.reflect.*

  // should be generated
  // if isQuotesType22(tree, StringLiteral(typeName))
  //   then Option(tree.asInstanceOf[quotes.reflect.Apply])
  //   else None

  val rr = TypeRepr.of[T]
  println(s"%%% toQuotesType22  T: $rr  ${rr.show}")
  println(s"%%% toQuotesType22  T: ${Type.show[T]}")

  val vTerm: Term = v.asTerm

  println(s"%%% v: $v $vTerm")

  //val srcInlined: Inlined = v.asTerm.asInstanceOf[Inlined]
  //val asInstanceOfMethod = Symbol.newMethod(Symbol.noSymbol, "asInstanceOf", TypeRepr.of[T])
  //val fun = Select(srcInlined.body, asInstanceOfMethod)
  //val typeApply = TypeApply(fun, List(TypeTree.of[T]))
  //val resInlined = Inlined(srcInlined.call, srcInlined.bindings, typeApply)
  //resInlined.asExprOf[T]

  val condIsQuotesType = applyIsQuotesType(vTerm, Type.show[T])
  val _applyOption = applyOption[T](applyInstanceOf[T](vTerm))
  val _noneOption = noneOption

  val _if = If(condIsQuotesType, _applyOption, _noneOption)

  val resExpr = _if.asExprOf[Option[T]]
  println(s"resExpr: ${resExpr.show}")
  resExpr


inline def toQuotesType33[T, T2](inline v: Any, inline t2: T2): Any =
  println(s"%%% toQuotesType33 => t2: $t2")
  v

inline def toQuotesType34[T](inline v: Any, inline t2: Any): Any =
  println(s"%%% toQuotesType34 => t2: $t2  ${t2.getClass.nn.getName}")
  v

inline def toQuotesType35[T](inline v: Any, inline t2: Any): Any =
  println(s"%%% toQuotesType34 => t2: $t2  ${t2.getClass.nn.getName}")
  v

inline def toQuotesType36[T](inline v: Any, inline t2: Any): T =
  println(s"%%% toQuotesType36 => t2: $t2  ${t2.getClass.nn.getName}")
  v.asInstanceOf[T]

private def positionToString(using q: Quotes)(p: q.reflect.Position): String =
  s"Position { start: ${p.start}, end: ${p.end}," +
    s" startLine: ${p.startLine}, endLine: ${p.endLine}," +
    s" startColumn: ${p.startColumn}, endColumn: ${p.endColumn}," +
    s" sourceFile: ${p.sourceFile}, sourceCode: ${p.sourceCode}" +
    s" }"


def applyInstanceOf[T](using q: Quotes)(using Type[T])(expr: q.reflect.Term): q.reflect.Term =
  import q.reflect.{ Symbol, TypeTree, Select, TypeRepr, TypeApply }
  val asInstanceOfMethod = Symbol.newMethod(Symbol.noSymbol, "asInstanceOf", TypeRepr.of[T])
  val fun = Select(expr, asInstanceOfMethod)
  val typeApply = TypeApply(fun, List(TypeTree.of[T]))
  typeApply

// [scala.Option.apply[java.lang.String](v)], as term
// Apply(
//    TypeApply(
//      Select( Ident(Option), apply ),
//      List( TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class lang)),class String)] )
//    ),
//    List( Ident(v) )
//  )
//
def applyOption[T](using q: Quotes)(using Type[T])(expr: q.reflect.Term): q.reflect.Term =
  import q.reflect.*
  val scalaPackageIdent = Ident(Symbol.requiredPackage("scala").termRef)
  val optionApplySelect = Select.unique(Select.unique(scalaPackageIdent, "Option"), "apply")
  val typeApply = TypeApply(optionApplySelect, List(TypeTree.of[T]))
  Apply(typeApply, List(expr))


def noneOption(using q: Quotes): q.reflect.Term =
  import q.reflect.{ Ident, Symbol, Select }
  Select.unique(Ident(Symbol.requiredPackage("scala").termRef), "None")


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
  val funPackage = Select.unique(Select.unique(Select.unique(Select.unique(rootPackageIdent, "mvv"), "scala"), "quotes"),
    "quotesTypesStuff$package")
  val methodType = MethodType(List("el", "typeName"))
    (paramInfosExp => List[TypeRepr](TypeRepr.of[Any], TypeRepr.of[String]), resultTypeExp => TypeRepr.of[Boolean])
  val fun = Symbol.newMethod(Symbol.noSymbol, "isQuotesType", methodType)
  val funSelect = Select(funPackage, fun)
  val apply = Apply(funSelect, List(el, Literal(StringConstant(quoteTypeName))))
  apply
