package org.mvv.scala.tools.props

import scala.quoted.{Expr, Quotes, Type }
import org.mvv.scala.tools.quotes.{asValDef, qStringLiteral}



/** Returns left (lhs, left-hand side) variable name.
 *  For example {{{ val var1 = leftVarName }}}
 *  In this case 'var1' will contain "var1".
 *
 *  It is mainly just macros-example. I do not see real use of it.
 */
inline def lhsMacroVarName: String =
  ${ lhsMacroVarNameImpl }


/** Returns left variable name without '_' suffix/prefix. */
inline def strippedLhsMacroVarName: String =
  ${ strippedLhsMacroVarNameImpl }


/** Creates late-init prop with 'prop name' of left variable */
inline def lateInitGeneralProp[T]: PropertyValue[T] =
  PropertyValue[T](strippedLhsMacroVarName, changeable = false)

/*

def aaa[X](): Unit = {

  type Elem[X] = X match
    case String => Char
    case Array[t] => t
    case Iterable[t] => t
}


enum DbType :
  case DbInt
  case DbString

private def getDbType(using q: Quotes)(typeRepr: q.reflect.TypeRepr): DbType =
  import q.reflect.*
  import scala.quoted.Type
  typeRepr.asType match {
    case '[Int] => DbType.DbInt // (1)
    case '[String] => DbType.DbString
    case '[unknown] => // (2)
      q.reflect.report.errorAndAbort("Unsupported type as DB column " + Type.show[unknown])
}
*/


/** Creates late-init prop with 'prop name' of left variable.
 *
 *  It is mainly designed for easy testing because there is no big sense to use late-int property
 *  if you initially know variable value.
 */
inline def lateInitGeneralProp[T](v: T): PropertyValue[T] =
  PropertyValue[T](strippedLhsMacroVarName, v, changeable = false)


/** Creates late-init prop with 'prop name' of left variable */
inline def lateInitOptionProp[T]: PropertyValue[Option[T]] =
  PropertyValue[Option[T]](strippedLhsMacroVarName, None, uninitializedValue = None, changeable = false)


/** Creates late-init prop with 'prop name' of left variable.
 *
 *  It is mainly designed for easy testing because there is no big sense to use late-int property
 *  if you initially know variable value.
 */
inline def lateInitOptionProp[T](v: Option[T]): PropertyValue[Option[T]] =
  PropertyValue[Option[T]](strippedLhsMacroVarName, v, uninitializedValue = None, changeable = false)


private inline def uninitializedValueOf[T]: T|Null = inline if isOptionType[T] then None.asInstanceOf[T] else null

/** Universal version for usual types and Option  */
inline def lateInitProp[T]: PropertyValue[T] =
  PropertyValue[T](strippedLhsMacroVarName, uninitializedValue = uninitializedValueOf[T], changeable = false)

/** Universal version for usual types and Option  */
inline def lateInitProp[T](v: T): PropertyValue[T] =
  PropertyValue[T](strippedLhsMacroVarName, v, uninitializedValue = uninitializedValueOf[T], changeable = false)


private inline def isOptionType[T]: Boolean =
  ${ isOptionTypeImpl[T] }

private def isOptionTypeImpl[T](using q: Quotes)(using Type[T]): Expr[Boolean] =
  import q.reflect.TypeRepr
  val isBool = TypeRepr.of[T] <:< TypeRepr.of[Option[?]]
  if isBool then '{ true } else '{ false }


private def lhsMacroVarNameImpl(using q: Quotes): Expr[String] =
  qStringLiteral( getLeftVarNameOfMacrosImpl(false) ).asExprOf[String]

private def strippedLhsMacroVarNameImpl(using q: Quotes): Expr[String] =
  qStringLiteral( getLeftVarNameOfMacrosImpl(true) ).asExprOf[String]


private def getLeftVarNameOfMacrosImpl(using q: Quotes)(toStripUnderscoreSuffixPrefix: Boolean): String =
  import q.reflect.{ Symbol, Flags, report }
  import q.reflect.Symbol.noSymbol
  import org.mvv.scala.tools.quotes.{ isOneOf, symbolDetailsToString }
  //noinspection ScalaUnusedSymbol
  given CanEqual[Symbol, Symbol] = CanEqual.derived

  var s = Symbol.spliceOwner
  var valDefSymbol = noSymbol

  while s != noSymbol && valDefSymbol == noSymbol do
    if s.isValDef && !s.flags.isOneOf(Flags.Macro, Flags.Synthetic) then valDefSymbol = s
    s = s.maybeOwner

  if valDefSymbol == noSymbol then
    val errMsg = s"Owning variable of 'lateInitProp' is not found."
    report.error(errMsg); throw IllegalStateException(errMsg)

  val lateInitPropOwningVarName: String = valDefSymbol.tree.asValDef.name
  val resultVarName = if toStripUnderscoreSuffixPrefix
                      then lateInitPropOwningVarName.stripPrefix("_").stripSuffix("_")
                      else lateInitPropOwningVarName
  resultVarName
