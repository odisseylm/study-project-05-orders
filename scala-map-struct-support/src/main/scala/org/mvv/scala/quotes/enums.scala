package org.mvv.scala.quotes

import scala.quoted.{Quotes, Type, Expr }
import scala.reflect.Enum as ScalaEnum
import java.lang.reflect.Method as JavaMethod
//
import org.mvv.scala.mapstruct.lastAfter


/**
 * If using it as extension methods causes problems (and you need to pass all implicits manually)
 * use it as non-extension like ' valueOrAbort[EnumValueSelectMode](enumValueSelectModeExpr) '
 */
// !! (using Quotes, Type[EnumType]) should be on every method (not on 'extension' line) !!
// Otherwise these methods cannot be used as extension (with automatic implicits resolving)
extension [EnumType <: ScalaEnum](enumExpr: Expr[EnumType])
  //noinspection ScalaUnusedSymbol
  def enumNameOrAbort(using Quotes, Type[EnumType]): String = extractEnumValueName[EnumType](enumExpr)
  // May cause 'ambiguous' problems. In this case use enumValueOrAbort
  //noinspection ScalaUnusedSymbol
  def valueOrAbort(using Quotes, Type[EnumType]): EnumType = extractEnumValue[EnumType](enumExpr)
  //noinspection ScalaUnusedSymbol
  def enumValueOrAbort(using Quotes, Type[EnumType]): EnumType = extractEnumValue[EnumType](enumExpr)



def enumValueNames[EnumType <: ScalaEnum]
  (using q: Quotes) (using Type[EnumType]): List[String] =

  import q.reflect.Symbol

  val classSymbol: Symbol = Symbol.classSymbol(Type.show[EnumType]) // Symbol.requiredClass(typeNameStr)
  val children: List[Symbol] = classSymbol.children

  // maybe with complex enums we need to filter out non-enum items
  val enumNames: List[String] = children.map(_.name)
  enumNames



//noinspection ScalaUnusedSymbol
def enumValueEntries[EnumType <: ScalaEnum]
  (using q: Quotes) (using Type[EnumType]): List[(String, q.reflect.Term)] =

  import q.reflect.Symbol
  val enumNames: List[String] = enumValueNames[EnumType]
  val enumValues = enumNames.map(enumValueName => (enumValueName, enumValue[EnumType](enumValueName)))
  enumValues



//noinspection ScalaUnusedSymbol
def enumValues[EnumType <: ScalaEnum]
  (using q: Quotes) (using Type[EnumType]): List[q.reflect.Term] =
  val enumValues: List[q.reflect.Term] = enumValueEntries[EnumType].map(_._2)
  enumValues



//noinspection ScalaUnusedSymbol , NoTailRecursionAnnotation // there is no recursion at all
def enumValue[EnumType <: ScalaEnum]
  (using q: Quotes) (using Type[EnumType])
  (enumValueName: String): q.reflect.Term =
  enumValue[EnumType](enumValueName, ClassSelectMode.ByFullClassName)

def enumValue[EnumType <: ScalaEnum]
  (using q: Quotes) (using Type[EnumType])
  (enumValueName: String, classSelectMode: ClassSelectMode): q.reflect.Term =

  // This simple/logical approach does not work... as usual/expected with scala3 macros :-(
  //val classSymbol: Symbol = TypeRepr.of[T].classSymbol.get // typeRepr.typeSymbol
  //return Select.unique(Ident(classSymbol.termRef), enumValueName)
  //
  // It also does not work
  //return Select.unique(Ref.term(TypeRepr.of[T].termSymbol.termRef), enumValueName)

  val classSelect = qClassNameOf[EnumType](classSelectMode)
  q.reflect.Select.unique(classSelect, enumValueName)



/**
 * Standard scala Expr.value/valueOrAbort do not work for non primitives
 * For very simple cases you can use the following easy/universal approach with 'matches'.
 *
 * val enumValue = enumExpr match
 *   case sm if sm.matches('{ SelectEnumMode.ByEnumFullClassName }) => SelectEnumMode.ByEnumFullClassName
 *   case sm if sm.matches('{ SelectEnumMode.ByEnumClassThisType }) => SelectEnumMode.ByEnumClassThisType
 *   case other => report.errorAndAbort(s"Unexpected/unparseable selectEnumMode [$other].")
 *
 * But it may be a bit boring if you need it often and in this case you can use these methods
 * extractEnumValueName/extractEnumValue.
 * It is up to you which approach to use.
 *
 * Getting real enum value (or enum value name) of some enums is not always possible.
 * This enum should be already compiled or be internal, which is used inside macros.
 * Mainly is designed for working with internal enums which are used in macros.
 * These enums should be located in proper independent source files
 * to be pre-compiled before macros expansion (which uses these enums).
 */
private def extractEnumValueName[EnumType <: ScalaEnum]
  (using q: Quotes) (using Type[EnumType])
  (enumExpr: Expr[EnumType]): String =

  import q.reflect.{ Term, TypeRepr, report }

  val _enumValues: List[(String, Term)] = enumValueEntries[EnumType]
  val _enumValue: Option[(String, Term)] = _enumValues
    .find(en => enumExpr.matches(en._2.asExprOf[EnumType]))
  val _enumValueName: String = _enumValue.map(_._1).getOrElse { report.errorAndAbort(
      s"Expression $enumExpr [${enumExpr.show}] cannot be converted to enum value of ${TypeRepr.of[EnumType].show}")
  }
  _enumValueName



//noinspection ScalaUnusedSymbol
private def extractEnumValue[EnumType <: ScalaEnum]
  (using Quotes, Type[EnumType])
  (enumExpr: Expr[EnumType]): EnumType =
  val _enumName = extractEnumValueName[EnumType](enumExpr)
  enumValueOfMethod[EnumType](_enumName)



//noinspection ScalaUnnecessaryParentheses
def enumValueOfMethod[EnumType <: ScalaEnum]
  (using q: Quotes)(using Type[EnumType])
  : (String => EnumType) =

  val enumClassName = q.reflect.TypeRepr.of[EnumType].show
  val valueOfMethod: JavaMethod =
    try Class.forName(enumClassName).nn.getMethod("valueOf", classOf[String]).nn
    catch case _: Exception => Class.forName(enumClassName + "$").nn.getMethod("valueOf", classOf[String]).nn

  (_enumName: String) => valueOfMethod.invoke(null, _enumName).nn.asInstanceOf[EnumType]
