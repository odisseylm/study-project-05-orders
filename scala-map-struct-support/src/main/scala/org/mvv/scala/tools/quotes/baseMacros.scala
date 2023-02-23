package org.mvv.scala.tools.quotes

import scala.quoted.{ Quotes, Expr }



inline def currentPackage: String =
  ${ currentPackageImpl }

inline def topClassOrModuleFullName: String =
  ${ topClassOrModuleFullNameImpl }



def currentPackageImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qCurrentExprPackage)

def topClassOrModuleFullNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopClassOrModuleFullName)
