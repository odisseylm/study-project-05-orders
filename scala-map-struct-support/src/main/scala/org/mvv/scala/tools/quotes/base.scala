package org.mvv.scala.tools.quotes

import scala.quoted.Quotes



def refName(using q: Quotes)(ref: q.reflect.Ref): String =
  import q.reflect.Ident
  ref match
    case Ident(name: String) => name
    case _ =>
      log.warn(s"Ref is not Ident and name is taken by symbol name." +
        s" It may be unexpected behavior which should be better treated in proper non-default way (ref: $ref).")
      ref.symbol.name


def fullPackageName(using q: Quotes)(packageClause: q.reflect.PackageClause): String =
  val fullPackageName = packageClause.symbol.fullName
  if fullPackageName == "<empty>" then "" else fullPackageName
