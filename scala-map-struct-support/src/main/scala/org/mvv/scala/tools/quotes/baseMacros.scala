package org.mvv.scala.tools.quotes

import scala.quoted.{ Quotes, Expr, Type }
import org.mvv.scala.tools.{ afterLastOrOrigin, replacePrefix, stripAfter }
import org.mvv.scala.tools.KeepDelimiter.ExcludeDelimiter




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

/** It is designed ONLY for logging */
inline def classNameOf[T]: String =
  ${ classNameOfImpl[T] }



private def currentPackageImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qCurrentExprPackage)

private def topClassOrModuleFullNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopClassOrModuleFullName)

private def topMethodFullNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopMethodFullName)

private def topMethodSimpleNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopMethodFullName.afterLastOrOrigin("."))

private def classNameOfImpl[T](using q: Quotes)(using Type[T]): Expr[String] =
  // this logic is copy-pasted from quotesClassPrimitives.scala
  // but I do not want to have any (risky) dependencies in this very base module.
  import q.reflect.TypeRepr
  val typeRepr: TypeRepr = TypeRepr.of[T]
  val rawTypeName: String = typeRepr.dealias.widen.dealias.show
  val fullClassName = rawTypeName
    .stripAfter("[", ExcludeDelimiter)
    .replacePrefix("_root_.scala.", "scala.")
  qStringLiteralExpr(fullClassName)
