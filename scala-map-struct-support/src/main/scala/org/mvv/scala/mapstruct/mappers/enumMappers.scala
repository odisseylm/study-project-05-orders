package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.Enum as ScalaEnum
//
import org.mvv.scala.mapstruct.Logger


private val log: Logger = Logger("org.mvv.scala.mapstruct.mappers.enumMappers")


inline def enumMappingFunc[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (): EnumFrom => EnumTo = ${ enumMappingFuncImpl[EnumFrom, EnumTo]() }


def enumMappingFuncImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  ()
  (using quotes: Quotes)(using etFrom: Type[EnumFrom])(using etTo: Type[EnumTo]):
    Expr[EnumFrom => EnumTo] =

  import quotes.reflect.*

  val enumFromClassName: String = Type.show[EnumFrom]
  val enumToClassName: String = Type.show[EnumTo]

  val logPrefix = s"enumMappingFuncImpl [ $enumFromClassName => $enumToClassName ], "


  def enumValues[EnumType <: ScalaEnum](using Type[EnumType]): List[String] =
    val classSymbol: Symbol = Symbol.classSymbol(Type.show[EnumType]) // Symbol.requiredClass(typeNameStr)
    val children: List[Symbol] = classSymbol.children
    // maybe with complex enums we need to filter out non-enum items
    val enumNames: List[String] = children.map(_.name)
    enumNames

  val useFullEnumClassName = true
  def enumValue[EnumType <: ScalaEnum](using Type[EnumType])(enumValue: String): Term =
    if useFullEnumClassName
      then enumValueUsingFullClassName[EnumType](enumValue)
      else enumValueUsingSimpleClassNameAndEnumClassThisScope[EnumType](enumValue)

  val enumFromValues = enumValues[EnumFrom]
  val enumToValues = enumValues[EnumTo]
  val allEnumValues = (enumFromValues ++ enumToValues).distinct

  log.trace(s"$logPrefix => enumFromValues: $enumFromValues")
  log.trace(s"$logPrefix => enumToValues  : $enumToValues")
  log.trace(s"$logPrefix => allEnumValues : $allEnumValues")

  def unexpectedEnumValuesErrorMsg(enumClassName: String, unexpectedEnumValues: IterableOnce[String]): Option[String] =
    val unexpectedEnumValuesIt = unexpectedEnumValues.iterator
    if unexpectedEnumValuesIt.nonEmpty
      then Option(s"Enum [$enumClassName] has non-mapped values ${unexpectedEnumValuesIt.mkString("[", ", ", "]")}")
      else None

  val unexpectedEnumFromValues: List[String] = enumFromValues diff enumToValues
  val unexpectedEnumToValues:   List[String] = enumToValues   diff enumFromValues

  val err1: Option[String] = unexpectedEnumValuesErrorMsg(enumFromClassName, unexpectedEnumFromValues)
  val err2: Option[String] = unexpectedEnumValuesErrorMsg(enumToClassName, unexpectedEnumToValues)
  val allErrors = List(err1, err2).filter(_.isDefined).map(_.get).mkString(", ")

  if allErrors.nonEmpty then
    // ?? or better to use error() an return something??
    report.errorAndAbort(allErrors)

  // if enums are not symmetric it will cause compilation error of generated (by this macro) scala code
  // what is expected/desired behavior

  val rhsFn: (Symbol, List[Tree]) => Tree = (s: Symbol, paramsAsTrees: List[Tree]) => {
    log.trace(s"$logPrefix rhsFn { s: $s, $paramsAsTrees }")

    val caseDefs: List[CaseDef] = allEnumValues.map(enumValueLabel =>
      val selectFrom = enumValue[EnumFrom](enumValueLabel)
      log.trace(s"$logPrefix selectFrom: $selectFrom")

      val selectTo = enumValue[EnumTo](enumValueLabel)
      log.trace(s"$logPrefix selectFrom: $selectTo")

      val caseDef = CaseDef(
        selectFrom,
        None,
        Block(
          Nil,
          selectTo,
        )
      )
      log.trace(s"$logPrefix caseDef: $caseDef")
      caseDef
    )

    val matchExpr: Match = Match(paramsAsTrees.head.asInstanceOf[Term], caseDefs)
    log.trace(s"$logPrefix matchExpr: $matchExpr")

    matchExpr
  }


  val anonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(
      List("enumFromValue")) ( // parameter names
      _ => List(TypeRepr.of[EnumFrom]),
      _ => TypeRepr.of[EnumTo],
    ),
    rhsFn
  )

  println(s"anonFunLambda expr: ${anonFunLambda.asExprOf[EnumFrom => EnumTo].show}")

  val inlined = Inlined(None, Nil, anonFunLambda)
  val inlinedExpr = inlined.asExprOf[EnumFrom => EnumTo]
  println(s"${inlinedExpr.show}")
  inlinedExpr


def enumValueUsingSimpleClassNameAndEnumClassThisScope[T <: ScalaEnum](using quotes: Quotes)(using enumType: Type[T])(enumValueName: String): quotes.reflect.Term =
  ???

def enumValueUsingFullClassName[T <: ScalaEnum](using quotes: Quotes)(using enumType: Type[T])(enumValueName: String): quotes.reflect.Term =
  import quotes.reflect.*
  val fullClassName: String = TypeRepr.of[T].widen.show

  val parts: List[String] = fullClassName.split('.').toList
  require(parts.nonEmpty, s"Invalid enum class [$fullClassName].")

  if parts.sizeIs == 1 then
    // It does not work... as usual :-(
    // val typeRepr = TypeRepr.of[T]
    // val classSymbol = typeRepr.typeSymbol // typeRepr.typeSymbol
    // return Select.unique(Ident(classSymbol.termRef), enumValueName)

    // defn.RootPackage/_root_/<root> does not work for it.
    // Scala uses special 'empty' package for this purpose.
    // See some details in a bit deprecated scala-reflect-2.13.8-sources.jar!/scala/reflect/internal/StdNames.scala
    val emptyPackageIdent = Ident(Symbol.requiredPackage("<empty>").termRef)
    val classSelect = Select.unique(emptyPackageIdent, fullClassName)
    return Select.unique(classSelect, enumValueName)

  val rootPackageIdentCom = Ident(Symbol.requiredPackage(parts.head).termRef)
  val resultingFullClassNameSelect = parts.tail.tail.foldLeft
    (Select.unique(rootPackageIdentCom, parts.tail.head))
    ((preSelect, nextPart) => Select.unique(preSelect, nextPart))
  Select.unique(resultingFullClassNameSelect, enumValueName)

