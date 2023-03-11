package org.mvv.scala.tools.quotes

import scala.quoted.{ Quotes, Expr }
//
import org.mvv.scala.tools.{ tryDo, stripAfter }
import org.mvv.scala.tools.KeepDelimiter.ExcludeDelimiter



// only for showing in error message
def macrosSource(using q: Quotes): Any =
  import q.reflect.Symbol
  tryDo { Symbol.spliceOwner.owner.tree }.getOrElse(Symbol.spliceOwner.owner.pos)



def reportedFailedExprAsText(using q: Quotes)(expr: Expr[Any]): String =
  import q.reflect.Position
  Position.ofMacroExpansion.sourceCode
    .map(sourceCode => s"${expr.show} used in $sourceCode")
    .getOrElse(expr.show)



/** It is designed ONLY for logging */
def qTopClassOrModuleFullName(using q: Quotes): String =
  import q.reflect.Symbol
  given CanEqual[Symbol, Symbol] = CanEqual.derived

  var lastNonPackageFullName = ""

  var s = Symbol.spliceOwner
  while s != Symbol.noSymbol && !s.isPackageDef do
    if s.isClassDef || s.isTypeDef then lastNonPackageFullName = s.fullName
    s = s.maybeOwner

  val topClassOrModuleFullName = lastNonPackageFullName.stripAfter("$", ExcludeDelimiter)
  topClassOrModuleFullName


/** It is designed ONLY for logging */
def qTopMethodFullName(using q: Quotes): String =
  import q.reflect.Symbol
  given CanEqual[Symbol, Symbol] = CanEqual.derived

  var lastMethodFullName = ""

  var s = Symbol.spliceOwner
  while s != Symbol.noSymbol && !s.isPackageDef do
    if s.isDefDef then
      lastMethodFullName = s.fullName
    s = s.maybeOwner

  val codeLikeMethodFullName =lastMethodFullName.replace("$package$.", ".").nn
  codeLikeMethodFullName
