package org.mvv.scala.tools.quotes

import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ tryDo, stripAfter }
import org.mvv.scala.tools.KeepDelimiter.ExcludeDelimiter



// only for showing in error message
def macrosSource(using q: Quotes): Any =
  import q.reflect.Symbol
  tryDo { Symbol.spliceOwner.owner.tree }.getOrElse(Symbol.spliceOwner.owner.pos)



/** It is designed ONLY for logging */
def qTopClassOrModuleFullName(using q: Quotes): String =
  import q.reflect.Symbol

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

  var lastMethodFullName = ""

  var s = Symbol.spliceOwner
  while s != Symbol.noSymbol && !s.isPackageDef do
    if s.isDefDef then
      lastMethodFullName = s.fullName
    s = s.maybeOwner

  val codeLikeMethodFullName =lastMethodFullName.replace("$package$.", ".").nn
  codeLikeMethodFullName
