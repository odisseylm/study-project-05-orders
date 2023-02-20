package org.mvv.scala.quotes

import scala.quoted.*
//
import org.mvv.scala.tools.lastAfter



enum ClassSelectMode :
  case
    /** Recursive quotes 'Select' is used for every part of package/className/enumValue. */
    ByFullClassName
    /** Tricky but interesting approach :-) */
  , ByClassThisType



/** Returns tuple with package and simple class name */
def extractFullClassNameComponents (classFullName: String): (String, String) =
  val index = classFullName.lastIndexOf('.')
  if index == -1 then ("", classFullName)
  else (classFullName.substring(0, index).nn, classFullName.substring(index + 1).nn)



// TODO: cover with unit tests
/** This logic moved to separate method because probably it should be fixed
 * for generics/anonymous/etc */
def qFullClassNameOf[T]
  (using q: Quotes) (using Type[T])
  : String =

  import q.reflect.TypeRepr

  val typeRepr: TypeRepr = TypeRepr.of[T]
  val rawTypeName: String = typeRepr.widen.show
  val fullClassName = getFullClassName(rawTypeName)
  fullClassName


// TODO: cover with unit tests
/** This logic moved to separate method because probably it should be fixed
 * for generics/anonymous/etc */
//noinspection ScalaUnusedSymbol
def getFullClassName(using Quotes)(typeName: String) =
  // T O D O: impl if needed for generics/anonymous/etc
  typeName



def qScalaPackage (using q: Quotes): q.reflect.Term =
  import q.reflect.{ defn, Ident }
  val scalaPackageSymbol = defn.ScalaPackage // or Symbol.requiredPackage("scala")
  Ident(scalaPackageSymbol.termRef)



def qPackage (using q: Quotes) (_package: String): q.reflect.Term =
  import q.reflect.{ Symbol, Ident, Select }

  val parts: List[String] = _package.split('.')
    // scala splits "" to 1 item !!??
    .filter(_.nonEmpty)
    .toList

  if parts.isEmpty then
    // defn.RootPackage/_root_/<root> does not work for it.
    // Scala uses special 'empty' package for this purpose.
    // See some details in a bit deprecated scala-reflect-2.13.8-sources.jar!/scala/reflect/internal/StdNames.scala
    return Ident(Symbol.requiredPackage("<empty>").termRef)

  val rootPackageIdentCom = Ident(Symbol.requiredPackage(parts.head).termRef)
  val resultingPackageSelect = parts.tail.tail.foldLeft
    ( Select.unique(rootPackageIdentCom, parts.tail.head) )
    ( (preSelect, nextPart) => Select.unique(preSelect, nextPart) )
  resultingPackageSelect



//noinspection ScalaUnusedSymbol , NoTailRecursionAnnotation // there is no recursion at all
def qClassNameOf[T](using q: Quotes) (using Type[T])
  : q.reflect.Term =
  qClassNameOf[T](ClassSelectMode.ByFullClassName)



//noinspection ScalaUnusedSymbol
def qClassNameOf[T](using q: Quotes)(using Type[T])
                   (classSelectMode: ClassSelectMode): q.reflect.Term =

  // This simple/logical approach usage of 'class' does not work... as usual/expected with scala3 macros :-(
  //val classSymbol: Symbol = TypeRepr.of[T].classSymbol.get // typeRepr.typeSymbol
  //return Select.unique(Ident(classSymbol.termRef), enumValueName)
  //
  // It also does not work
  //return Select.unique(Ref.term(TypeRepr.of[T].termSymbol.termRef), enumValueName)

  import q.reflect.{ Select, TypeRepr }
  if classSelectMode == ClassSelectMode.ByFullClassName
    then qClassName_byClassFullNameSelect[T]
    else qClassName_usingSimpleClassNameAndEnumClassThisScope[T]



//noinspection ScalaUnusedSymbol , NoTailRecursionAnnotation // there is no recursion at all
private def qClassName_byClassFullNameSelect[T]
  (using q: Quotes) (using Type[T])
  : q.reflect.Term =
  qClassName_byClassFullNameSelect(qFullClassNameOf[T])


private def qClassName_byClassFullNameSelect (using q: Quotes)
  (classFullName: String): q.reflect.Select =

  import q.reflect.Select
  val packageAndName: (String, String) = extractFullClassNameComponents(classFullName)
  val packageSelect = qPackage(packageAndName._1)
  Select.unique(packageSelect, packageAndName._2)



//noinspection ScalaUnusedSymbol
private def qClassName_usingSimpleClassNameAndEnumClassThisScope[T]
  (using q: Quotes) (using Type[T])
  : q.reflect.Term =

  import quotes.reflect.{ Symbol, TypeRepr, TermRef, Ident, Select }

  val typeRepr: TypeRepr = TypeRepr.of[T]
  val classSymbol: Symbol = typeRepr.typeSymbol // typeRepr.classSymbol.get

  val scopeTypRepr: TypeRepr = findClassThisScopeTypeRepr(classSymbol).get
  val fullEnumClassName = qFullClassNameOf[T]
  val simpleEnumClassName = fullEnumClassName.lastAfter('.').getOrElse(fullEnumClassName)
  val classTerm = TermRef(scopeTypRepr, simpleEnumClassName)
  Ident(classTerm)



def qInstanceOf[T](using q: Quotes)(using Type[T])(expr: q.reflect.Term): q.reflect.Term =
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
def qOption[T](using q: Quotes)(using Type[T])(expr: q.reflect.Term): q.reflect.Term =
  import q.reflect.*
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
