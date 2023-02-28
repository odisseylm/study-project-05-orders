package org.mvv.scala.tools.quotes

import scala.quoted.{ Quotes, Expr }
import org.mvv.scala.tools.afterLastOrOrigin



/** It is designed ONLY for logging */
inline def currentPackage: String =
  ${ currentPackageImpl }

/** It is designed ONLY for logging */
inline def topClassOrModuleFullName: String =
  ${ topClassOrModuleFullNameImpl }

/** It is designed ONLY for logging */
inline def topMethodFullName: String =
  ${ topMethodFullNameImpl }

/** It is designed ONLY for logging */
inline def topMethodSimpleName: String =
  ${ topMethodSimpleNameImpl }



private def currentPackageImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qCurrentExprPackage)

private def topClassOrModuleFullNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopClassOrModuleFullName)

private def topMethodFullNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopMethodFullName)

private def topMethodSimpleNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopMethodFullName.afterLastOrOrigin("."))
