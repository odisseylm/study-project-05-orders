package org.mvv.scala.tools.quotes

import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ afterLastOr, stripAfter, tryDo }
import org.mvv.scala.tools.KeepDelimiter.ExcludeDelimiter



def fullPackageName(using q: Quotes)(packageClause: q.reflect.PackageClause): String =
  val fullPackageName = packageClause.symbol.fullName
  if fullPackageName == "<empty>" then "" else fullPackageName



def classFullPackageName(using q: Quotes)(classDef: q.reflect.ClassDef): String =
  import q.reflect.{ Symbol, PackageClause }
  //noinspection ScalaUnusedSymbol
  given CanEqual[Symbol, Symbol] = CanEqual.derived

  var s = classDef.symbol
  while s != Symbol.noSymbol && !s.isPackageDef do
    s = s.maybeOwner

  require(s.isPackageDef, s"Package of classDef ${classDef.symbol.fullName} is not found.")
  // taking Symbol.tree causes error (not allowed operation)... strange
  val _package = s.fullName
  _package



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

  if parts.sizeIs == 1 then return Ident(Symbol.requiredPackage(parts.head).termRef)

  val rootPackageIdentCom = Ident(Symbol.requiredPackage(parts.head).termRef)
  val resultingPackageSelect = parts.tail.tail.foldLeft
    ( Select.unique(rootPackageIdentCom, parts.tail.head) )
    ( (preSelect, nextPart) => Select.unique(preSelect, nextPart) )
  resultingPackageSelect



def qCurrentExprPackage(using q: Quotes): String =
  import q.reflect.{ Symbol, report }
  //noinspection ScalaUnusedSymbol
  given CanEqual[Symbol, Symbol] = CanEqual.derived

  var s = Symbol.spliceOwner
  while s != Symbol.noSymbol && !s.isPackageDef do
    s = s.maybeOwner

  if s.isPackageDef
    then s.fullName
    else report.errorAndAbort(s"Cannot find package of expr $macrosSource.")
