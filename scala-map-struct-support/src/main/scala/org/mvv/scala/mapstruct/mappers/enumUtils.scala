package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Quotes, Type}
import scala.reflect.Enum as ScalaEnum
//
import org.mvv.scala.mapstruct.lastAfter



def enumValueUsingFullClassName[T <: ScalaEnum]
  (using quotes: Quotes)(using enumType: Type[T])
  (enumValueName: String): quotes.reflect.Term =
  import quotes.reflect.*

  // It does not work... as usual :-(
  //val typeRepr: TypeRepr = TypeRepr.of[T]
  //val classSymbol: Symbol = typeRepr.classSymbol.get // typeRepr.typeSymbol
  //return Select.unique(Ident(classSymbol.termRef), enumValueName)

  // It also does not work
  //val typeRepr: TypeRepr = TypeRepr.of[T]
  //return Select.unique(Ref.term(typeRepr.termSymbol.termRef), enumValueName)

  val fullClassName: String = TypeRepr.of[T].widen.show

  val parts: List[String] = fullClassName.split('.').toList
  require(parts.nonEmpty, s"Invalid enum class [$fullClassName].")

  if parts.sizeIs == 1 then
    // defn.RootPackage/_root_/<root> does not work for it.
    // Scala uses special 'empty' package for this purpose.
    // See some details in a bit deprecated scala-reflect-2.13.8-sources.jar!/scala/reflect/internal/StdNames.scala
    val emptyPackageIdent = Ident(Symbol.requiredPackage("<empty>").termRef)
    val classSelect = Select.unique(emptyPackageIdent, fullClassName)
    return Select.unique(classSelect, enumValueName)

  val rootPackageIdentCom = Ident(Symbol.requiredPackage(parts.head).termRef)
  val resultingFullClassNameSelect = parts.tail.tail.foldLeft
    ( Select.unique(rootPackageIdentCom, parts.tail.head) )
    ( (preSelect, nextPart) => Select.unique(preSelect, nextPart) )
  val enumValueSelect = Select.unique(resultingFullClassNameSelect, enumValueName)
  enumValueSelect



def enumValueUsingSimpleClassNameAndEnumClassThisScope[T <: ScalaEnum]
(using quotes: Quotes)(using enumType: Type[T])
(enumValueName: String): quotes.reflect.Term =

  import quotes.reflect.{ Symbol, TypeRepr, TermRef, Ident, Select }

  val typeRepr: TypeRepr = TypeRepr.of[T]
  val classSymbol: Symbol = typeRepr.typeSymbol // typeRepr.classSymbol.get

  val scopeTypRepr: TypeRepr = findClassThisScopeTypeRepr(classSymbol).get
  val fullEnumClassName = typeRepr.show
  val simpleEnumClassName = fullEnumClassName.lastAfter('.').getOrElse(fullEnumClassName)
  val classTerm = TermRef(scopeTypRepr, simpleEnumClassName)
  val enumValueSelect = Select.unique(Ident(classTerm), enumValueName)
  enumValueSelect


