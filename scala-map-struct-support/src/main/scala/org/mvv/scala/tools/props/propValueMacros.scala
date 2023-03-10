package org.mvv.scala.tools.props

import scala.quoted.{ Quotes, Expr }
import org.mvv.scala.tools.quotes.{ qStringLiteral, asValDef }



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
inline def lateInitProp[T]: PropertyValue[T] =
  PropertyValue[T](strippedLhsMacroVarName, changeable = false)


/** Creates late-init prop with 'prop name' of left variable.
 *
 *  It is mainly designed for easy testing because there is no big sense to use late-int property
 *  if you initially know variable value.
 */
inline def lateInitProp[T](v: T): PropertyValue[T] =
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


private def lhsMacroVarNameImpl(using q: Quotes): Expr[String] =
  qStringLiteral( getLeftVarNameOfMacrosImpl(false) ).asExprOf[String]

private def strippedLhsMacroVarNameImpl(using q: Quotes): Expr[String] =
  qStringLiteral( getLeftVarNameOfMacrosImpl(true) ).asExprOf[String]


private def getLeftVarNameOfMacrosImpl(using q: Quotes)(toStripUnderscoreSuffixPrefix: Boolean): String =
  import q.reflect.{ Symbol, Flags, report }
  import q.reflect.Symbol.noSymbol
  import org.mvv.scala.tools.quotes.{ isOneOf, symbolDetailsToString }

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
