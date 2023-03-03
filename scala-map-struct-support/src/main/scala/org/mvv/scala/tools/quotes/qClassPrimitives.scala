package org.mvv.scala.tools.quotes

import scala.quoted.*
import scala.reflect.ClassTag
//
import org.mvv.scala.tools.{ afterLastOr, afterLastOrOrigin, stripAfter, tryDo }
import org.mvv.scala.tools.KeepDelimiter.ExcludeDelimiter



enum ClassSelectMode :
  case
    /** Recursive quotes 'Select' is used for every part of package/className/enumValue. */
    ByFullClassName
    /** Tricky but interesting approach :-) */
  , ByClassThisType



/** Returns tuple with 'path' (package for top classes) and simple class name */
//noinspection DuplicatedCode
private def extractFullClassNameComponents (classFullName: String): (String, String) =
  val index = classFullName.lastIndexOf('.')
  if index == -1 then ("", classFullName)
  else (classFullName.substring(0, index).nn, classFullName.substring(index + 1).nn)



/** This logic moved to separate method because probably it should be fixed
 * for generics/anonymous/etc */
def fullClassNameOf[T]
  (using q: Quotes) (using Type[T])
  : String =

  import q.reflect.TypeRepr

  val typeRepr: TypeRepr = TypeRepr.of[T]
  val rawTypeName: String = typeRepr.widen.show
  val fullClassName = getFullClassName(rawTypeName)
  fullClassName



/** This logic moved to separate method because probably it should be fixed
 *  for generics/anonymous/etc */
def getFullClassName(typeName: String): String =
  val fullClassName = typeName.stripAfter("[", ExcludeDelimiter)
  fullClassName



/** This logic moved to separate method because probably it should be fixed
 *  for generics/anonymous/etc */
def getSimpleClassName(typeName: String): String =
  val simpleName = getFullClassName(typeName).afterLastOrOrigin(".")
  simpleName


def getFullClassName(using q: Quotes)(classDef: q.reflect.ClassDef): String =
  getFullClassName(classDef.symbol.fullName)



/** Use this method if you are not sure that class is surely exists.
 *  It is needed because attempt to non-existent class causes compilation error
 *  even if such code is wrapped by try/catch. */
//noinspection ScalaUnusedSymbol
def classExists(using Quotes)(classFullName: String): Boolean =
  val (_package, simpleName) = extractFullClassNameComponents(classFullName)

  val packageTerm = qPackage(_package)
  val packageSymbol = packageTerm.symbol
  if !packageSymbol.exists then return false

  val declaredTypeNames = packageSymbol.declaredTypes.map(_.name)
  val exists = declaredTypeNames.contains(simpleName)
  exists



/**
 * We have very strange situation with scala when we inspect objects/companions
 * 1) If we define only 'object' we have 2 symbols (one with name of object and another with suffix '$').
 *    To inspect we need to use symbol with '$' and we MUST get it as Symbol.classSymbol(fullPath$).
 *    You canNOT use Symbol.companionClass since it returns noSymbol (but without fatal error)
 * 2) However if you have scala class with companion you canNOT get companion as
 *    Symbol.classSymbol(fullPath$) (error)!!! You need to use Symbol.companionClass
 *
 * How to distinguish these cases?? There is some hacking experimental approach.
 */
def getCompanionClass(using q: Quotes)(classOrPseudoClassSymbol: q.reflect.Symbol): Option[q.reflect.Symbol] =
  import q.reflect.Symbol
  // hacking approach :-(
  val isNormalClass = tryDo(classOrPseudoClassSymbol.methodMembers).nonEmpty
    && tryDo(classOrPseudoClassSymbol.tree).nonEmpty
  val companionClass =
    if isNormalClass then tryDo(classOrPseudoClassSymbol.companionClass)
      else
        val objectPath = classOrPseudoClassSymbol.fullName + "$"
        // 'classExists' is used because requesting (even only requesting in try/catch)
        // non-existent class may cause fatal unrecoverable error
        if classExists(objectPath) then tryDo(Symbol.classSymbol(objectPath)) else None
  companionClass



/**
 * Use it to get class term of class which is already passed as generic param (with 'using Type[T]').
 */
//noinspection ScalaUnusedSymbol , NoTailRecursionAnnotation // there is no recursion at all
def qClassNameOf[T](using q: Quotes) (using Type[T]): q.reflect.Term =
  qClassNameOf[T](ClassSelectMode.ByFullClassName)


/**
 * Use it for already compiled class (for example scala classes or classes from dependencies).
 */
//noinspection ScalaUnusedSymbol , NoTailRecursionAnnotation // there is no recursion at all
def qClassNameOfCompiled[T](using q: Quotes) (implicit classTag: ClassTag[T]): q.reflect.Term =
  qClassName(classTag.runtimeClass.getName.nn)



//noinspection ScalaUnusedSymbol
def qClassName(using q: Quotes) (fullClassName: String): q.reflect.Term =
  qClassName_byClassFullNameSelect(fullClassName)



// TODO: rename to qClassNameOfRuntimeClass
//noinspection ScalaUnusedSymbol
def qClassName(using q: Quotes) (cls: Class[?]): q.reflect.Term =
  qClassName_byClassFullNameSelect(cls.getName.nn)



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
    then qClassName_byClassFullNameSelect(fullClassNameOf[T])
    else qClassName_usingSimpleClassNameAndEnumClassThisScope[T]



// TODO: rename and make more general since it creates not only class (like qPathObject)
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

  val scopeTypRepr: TypeRepr = getClassThisScopeTypeRepr(classSymbol)
  val fullEnumClassName = fullClassNameOf[T]
  val simpleEnumClassName = fullEnumClassName.afterLastOr(".", fullEnumClassName)
  val classTerm = TermRef(scopeTypRepr, simpleEnumClassName)
  Ident(classTerm)
