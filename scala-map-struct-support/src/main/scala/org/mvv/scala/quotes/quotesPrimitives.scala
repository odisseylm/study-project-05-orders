package org.mvv.scala.quotes

import scala.quoted.*



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



//noinspection NoTailRecursionAnnotation, ScalaUnusedSymbol // there is no recusrsion at all,ScalaUnusedSymbol
def validateCastingFromTo[From, To](using Quotes, Type[From], Type[To])(): Unit =
  validateCastingFromTo[From, To](true)

def validateCastingFromTo[From, To](using q: Quotes)(using Type[From], Type[To])(abort: Boolean): Unit =
  import q.reflect.{ TypeRepr, report }
  val vTypeRepr = TypeRepr.of[From]
  val tTypeRepr = TypeRepr.of[To]
  val typesAreCompatible = tTypeRepr <:< vTypeRepr
  if !typesAreCompatible then
    val errMsg = s"Type ${tTypeRepr.show} cannot be cast to ${vTypeRepr.show}."
    if abort then report.errorAndAbort(errMsg) else report.error(errMsg)
