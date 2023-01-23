//noinspection DuplicatedCode
package com.mvv.scala.macros

import scala.Option
import scala.annotation.unused
import scala.compiletime.error
import scala.quoted.*
import scala.quoted.{Expr, Quotes, Type}
//
import com.mvv.scala.macros.Logger as log


// TODO: try to fix warning
inline def asPropValue[T](@unused inline expr: T): PropValue[T, Any] =
  ${ asPropValueImpl[T]('expr) }


private def asPropValueImpl[T](expr: Expr[T])(using t: Type[T])(using Quotes): Expr[PropValue[T, Any]] = {
  val exprText: String = expr.show
  log.debug(s"asPropValueImpl => expr: [$exprText]")

  val propName = extractPropName(expr)
  //noinspection ScalaUnusedSymbol
  val propNameExpr: Expr[String] = Expr(propName)

  val propValueExpr = '{ com.mvv.scala.macros.PropValue[T, Any]($propNameExpr, $expr) }

  log.debug(s"asPropValueImpl => resulting expr: [${propValueExpr.show}]")
  propValueExpr

  //'{ com.mvv.scala.macros.PropValue[T, Any]($propNameExpr, $expr, null) }
}


private def extractPropName(expr: Expr[?])(using Quotes): String =
  val exprText: String = expr.show
  val separator = ".this."
  // TODO: try to use Quotes to get rop name (as last AST node)
  val sepIndex = exprText.indexOf(separator)

  if (sepIndex == -1)
    import quotes.reflect.report
    //logCompilationError(s"Seems expression [$exprText] is not property.", expr)
    report.errorAndAbort(s"Seems expression [${failedExpressionText(expr)}] is not property.", expr)

  val propName = exprText.substring(sepIndex + separator.length).nn
  propName


private def failedExpressionText(expr: Expr[Any])(using Quotes): String =
  import quotes.reflect.Position
  Position.ofMacroExpansion.sourceCode
    .map(sourceCode => s"${expr.show} used in $sourceCode")
    .getOrElse(expr.show)


//noinspection ScalaUnusedSymbol
private def logCompilationError(errorMessage: String, expr: Expr[Any])(using Quotes) =
  import quotes.reflect.Position
  val pos = Position.ofMacroExpansion
  //log.error(s"Seems expression [$exprText] is not property..")
  log.error(errorMessage)
  log.error(s"Seems expression [${expr.show}] is not property..")
  log.error(s"  At ${pos.sourceFile}:${pos.startLine}:${pos.startColumn}")
  log.error(s"     ${pos.sourceFile}:${pos.endLine}:${pos.endColumn}")
  pos.sourceCode.foreach(v => log.error(s"     $v"))



//noinspection ScalaUnusedSymbol
// Debug functions
// temp
private def printFields(label: String, obj: Any): Unit =
  println(label)
  import scala.language.unsafeNulls
  allMethods(obj).foreach( printField(obj.getClass.getSimpleName, obj, _) )

private def printField(label: String, obj: Any, prop: String): Unit =
  try { println(s"$label.$prop: ${ getProp(obj, prop) }") } catch { case _: Exception => }

private def allMethods(obj: Any): List[String] =
  import scala.language.unsafeNulls
  obj.getClass.getMethods .map(_.getName) .toList

private def getProp(obj: Any, method: String): Any = {
  import scala.language.unsafeNulls
  val methodMethod = try { obj.getClass.getDeclaredMethod(method) } catch { case _: Exception => obj.getClass.getMethod(method) }
  val v = methodMethod.invoke(obj)
  //noinspection TypeCheckCanBeMatch
  if (v.isInstanceOf[Iterator[Any]]) {
    v.asInstanceOf[Iterator[Any]].toList
  } else v
}
