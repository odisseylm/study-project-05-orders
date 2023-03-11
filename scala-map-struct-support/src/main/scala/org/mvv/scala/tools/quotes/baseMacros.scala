package org.mvv.scala.tools.quotes

import scala.quoted.{Expr, Quotes, Type}
import org.mvv.scala.tools.{afterLastOrOrigin, replacePrefix, stripAfter}
import org.mvv.scala.tools.KeepDelimiter.ExcludeDelimiter

import scala.annotation.tailrec




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


/** It is designed for logging, error messages or using in macros. */
inline def classNameOf[T]: String =
  ${ classNameOfImpl[T] }

/** It is designed ONLY for logging (or error messages) */
inline def simpleClassNameOf[T]: String =
  ${ simpleClassNameOfImpl[T] }


/** It is designed for logging, error messages or using in macros. */
inline def typeNameOf[T]: String =
  ${ typeNameOfImpl[T] }
/** Similar to version without expr param but it can be used with 'expr' to implicitly get type 'T'. */
inline def typeNameOf[T](inline expr: T): String =
  ${ typeNameOfImpl[T] }

inline def simpleTypeNameOf[T]: String =
  ${ simpleTypeNameOfImpl[T] }
/** Similar to version without expr param but it can be used with 'expr' to implicitly get type 'T'. */
inline def simpleTypeNameOf[T](inline expr: T): String =
  ${ simpleTypeNameOfImpl[T] }

inline def underlyingTypeNameOf[T]: String =
  ${ underlyingTypeNameOfImpl[T] }
/** Similar to version without expr param but it can be used with 'expr' to implicitly get type 'T'. */
inline def underlyingTypeNameOf[T](inline expr: T): String =
  ${ underlyingTypeNameOfImpl[T] }


inline def underlyingSimpleTypeNameOf[T]: String =
  ${ underlyingSimpleTypeNameOfImpl[T] }
/** Similar to version without expr param but it can be used with 'expr' to implicitly get type 'T'. */
//noinspection ScalaUnusedSymbol
inline def underlyingSimpleTypeNameOf[T](inline expr: T): String =
  ${ underlyingSimpleTypeNameOfImpl[T] }



private def currentPackageImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qCurrentExprPackage)

private def topClassOrModuleFullNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopClassOrModuleFullName)

private def topMethodFullNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopMethodFullName)

private def topMethodSimpleNameImpl(using q: Quotes): Expr[String] =
  qStringLiteralExpr(qTopMethodFullName.afterLastOrOrigin("."))



private def typeReprName(using q: Quotes)(typeRepr: q.reflect.TypeRepr): String =
  // this logic is copy-pasted from qClassPrimitives.scala
  // but I do not want to have any (risky) dependencies in this very base module.
  val rawTypeName: String = typeRepr.dealias.widen.dealias.show
  val fullTypeName = rawTypeName.replacePrefix("_root_.scala.", "scala.")
  fullTypeName


private def classNameOfImpl_[T](using q: Quotes)(using Type[T]): String =
  val typeName = typeReprName(q.reflect.TypeRepr.of[T])
  val className = typeName.stripAfter("[", ExcludeDelimiter)
  className


private def classNameOfImpl[T](using q: Quotes)(using Type[T]): Expr[String] =
  qStringLiteralExpr(classNameOfImpl_[T])

private def simpleClassNameOfImpl[T](using q: Quotes)(using Type[T]): Expr[String] =
  val className = classNameOfImpl_[T]
  qStringLiteralExpr(className.afterLastOrOrigin("."))



private def typeNameOfImpl[T](using q: Quotes)(using Type[T]): Expr[String] = {
  val typeName = typeReprName(q.reflect.TypeRepr.of[T])
  qStringLiteralExpr(typeName)
}

private def simpleTypeNameOfImpl[T](using q: Quotes)(using Type[T]): Expr[String] = {
  val typeName = typeReprName(q.reflect.TypeRepr.of[T])
  val simpleTypeName = getSimpleTypeName(typeName)
  qStringLiteralExpr(simpleTypeName)
}

private def underlyingTypeNameOfImpl_[T](using q: Quotes)(using Type[T]): String =
  import q.reflect.TypeRepr
  val underlyingType: TypeRepr = findUnderlyingType(TypeRepr.of[T])
  val underlyingTypeName = typeReprName(underlyingType)
  underlyingTypeName

private def underlyingTypeNameOfImpl[T](using Quotes, Type[T]): Expr[String] =
  qStringLiteralExpr(underlyingTypeNameOfImpl_[T])

private def underlyingSimpleTypeNameOfImpl[T](using q: Quotes)(using Type[T]): Expr[String] =
  val underlyingTypeName = underlyingTypeNameOfImpl_[T]
  val underlyingSimpleTypeName = getSimpleTypeName(underlyingTypeName)
  qStringLiteralExpr(underlyingSimpleTypeName)


@tailrec
private def findUnderlyingType(using q: Quotes)(typeRepr: q.reflect.TypeRepr): q.reflect.TypeRepr =
  import q.reflect.{ AppliedType, TypeRepr }

  typeRepr match
    // Destructuring pattern matching works fine there but causes warning
    //   'pattern selector should be an instance of Matchable, but it has unmatchable type q.reflect.TypeRepr instead'
    //case AppliedType(_, typeArgs: List[TypeRepr]) =>
    //  if typeArgs.sizeIs == 1 then findUnderlyingType(typeArgs.head) else typeRepr

    case at: AppliedType =>
      if at.args.sizeIs == 1 then findUnderlyingType(at.args.head) else typeRepr
    case other => other


// TODO: rewrite it similar to approach in findUnderlyingType and apply 'simple naming' for every component of type tree
// (can be easily applied for AppliedType, AndType/OrType, TypeBounds, ?NoPrefix?)
/**
 * This very simple impl support ONLy the simplest types like
 * * Class1 => Class1
 * * com.mvv.Class1 => Class1
 * * com.mvv.Class1[com.mvv.Class2] => Class1[com.mvv.Class2] (even this works not good enough, see my 'T O D O' there)
 */
private def getSimpleTypeName(typeName: String): String =
  val indexOfGenericsType_ = typeName.indexOf('[')
  val indexOfGenericsTypeOrEnd = if indexOfGenericsType_ == -1 then typeName.length else indexOfGenericsType_

  val endOfPackage = typeName.lastIndexWhere(_ == '.', indexOfGenericsTypeOrEnd)
  val startOfSimpleName = if endOfPackage == -1 then 0 else endOfPackage + 1

  val simpleTypeName = typeName.substring(startOfSimpleName).nn
  simpleTypeName
