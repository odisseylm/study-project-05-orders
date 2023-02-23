package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.Enum as ScalaEnum
//
import org.mvv.scala.tools.{ Logger, afterLast, isOneOf, getByReflection, unwrapOption }
// for debug only
import org.mvv.scala.mapstruct.debug.dump.{ isImplClass, activeFlags, activeFlagEntries, dumpSymbol }
import org.mvv.scala.mapstruct.debug.printFields
import org.mvv.scala.tools.quotes.{ ClassSelectMode, enumValue, valueOrAbort, enumValueNames }


private val log: Logger = Logger("org.mvv.scala.mapstruct.mappers.enumMappers")


/*
Parameters may only be:
 * Quoted parameters or fields
 * Literal values of primitive types
 * References to `inline val`s
*/

inline def enumMappingFunc[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum](): EnumFrom => EnumTo =
  ${ enumMappingFuncImpl[EnumFrom, EnumTo]( '{ ClassSelectMode.ByFullClassName }, '{ Nil } ) }



//noinspection ScalaUnusedSymbol
inline def enumMappingFunc[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline classSelectMode: ClassSelectMode): EnumFrom => EnumTo =
  ${ enumMappingFuncImpl[EnumFrom, EnumTo]( 'classSelectMode, '{ Nil } ) }



//noinspection ScalaUnusedSymbol
inline def enumMappingFunc[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline classSelectMode: ClassSelectMode, inline customMappings: (EnumFrom, EnumTo)*): EnumFrom => EnumTo =
  ${ enumMappingFuncImpl[EnumFrom, EnumTo]( 'classSelectMode, 'customMappings ) }



def enumMappingFuncImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (classSelectModeExpr: Expr[ClassSelectMode], customMappings: Expr[Seq[(EnumFrom, EnumTo)]])
  (using q: Quotes)(using Type[EnumFrom], Type[EnumTo], Type[ClassSelectMode])
  : Expr[EnumFrom => EnumTo] =

  import q.reflect.*

  // unfortunately standard scala Expr.valueOrAbort  does not work (for complex types)
  val enumValueSelectMode = classSelectModeExpr.valueOrAbort

  val enumFromClassName: String = Type.show[EnumFrom]
  val enumToClassName:   String = Type.show[EnumTo]

  val logPrefix = s"enumMappingFuncImpl [ $enumFromClassName => $enumToClassName ], "

  val enumFromValues = enumValueNames[EnumFrom]
  val enumToValues = enumValueNames[EnumTo]
  val allEnumValues = (enumFromValues ++ enumToValues).distinct

  log.trace(s"$logPrefix => enumFromValues: $enumFromValues")
  log.trace(s"$logPrefix => enumToValues  : $enumToValues")
  log.trace(s"$logPrefix => allEnumValues : $allEnumValues")

  def unexpectedEnumValuesErrorMsg(enumClassName: String, unexpectedEnumValues: IterableOnce[String]): Option[String] =
    val unexpectedEnumValuesIt = unexpectedEnumValues.iterator
    if unexpectedEnumValuesIt.nonEmpty
      then Option(s"Enum [$enumClassName] has non-mapped values ${unexpectedEnumValuesIt.mkString("[", ", ", "]")}")
      else None

  val unexpectedByNameEnumFromValues: List[String] = enumFromValues diff enumToValues
  val unexpectedByNameEnumToValues:   List[String] = enumToValues   diff enumFromValues

  log.trace(s"$logPrefix customMappings: ${customMappings.asTerm}")

  val customMappingAsEnumNames: List[(String, String)] = extractCustomEnumMappingTuplesExpr[EnumFrom, EnumTo](customMappings)
  log.trace(s"$logPrefix customMappingAsEnumNames: $customMappingAsEnumNames")

  val customProcessedEnumFromValuesAll: List[String] = customMappingAsEnumNames.map(_._1)
  val customProcessedEnumToValuesAll: List[String]   = customMappingAsEnumNames.map(_._2)

  val customProcessedEnumFromValues: List[String] = customProcessedEnumFromValuesAll.distinct
  val customProcessedEnumToValues: List[String]   = customProcessedEnumToValuesAll.distinct

  if customProcessedEnumFromValuesAll != customProcessedEnumFromValues then
    val duplicated = customProcessedEnumFromValuesAll diff customProcessedEnumFromValues
    throw IllegalArgumentException(s"Seems custom mappers contain duplicates for [${duplicated.mkString(", ")}].")

  val unexpectedEnumFromValues: List[String] = unexpectedByNameEnumFromValues diff customProcessedEnumFromValues
  val unexpectedEnumToValues:   List[String] = unexpectedByNameEnumToValues   diff customProcessedEnumToValues

  val err1: Option[String] = unexpectedEnumValuesErrorMsg(enumFromClassName, unexpectedEnumFromValues)
  val err2: Option[String] = unexpectedEnumValuesErrorMsg(enumToClassName, unexpectedEnumToValues)
  val allErrors = List(err1, err2).filter(_.isDefined).map(_.get).mkString(", ")

  if allErrors.nonEmpty then
    // ?? or better to use error() an return something??
    report.errorAndAbort(allErrors)

  // if enums are not symmetric it will cause compilation error of generated (by this macro) scala code
  // what is expected/desired behavior

  val enumValuesForDefaultMappings = enumFromValues diff customProcessedEnumFromValues

  val rhsFn: (Symbol, List[Tree]) => Tree = (s: Symbol, paramsAsTrees: List[Tree]) => {
    log.trace(s"$logPrefix rhsFn { s: $s, $paramsAsTrees }")

    val allMappings =
      enumValuesForDefaultMappings.map(n => (n, n)) ++ customMappingAsEnumNames

    val caseDefs: List[CaseDef] = allMappings.map( (enumValueFromLabel, enumValueToLabel) =>
      val selectFrom = enumValue[EnumFrom](enumValueFromLabel, enumValueSelectMode)
      log.trace(s"$logPrefix selectFrom: $selectFrom")

      val selectTo = enumValue[EnumTo](enumValueToLabel, enumValueSelectMode)
      log.trace(s"$logPrefix selectFrom: $selectTo")

      val caseDef = CaseDef( selectFrom, None, Block( Nil, selectTo ) )
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

  log.trace(s"$logPrefix anonFunLambda expr: ${anonFunLambda.asExprOf[EnumFrom => EnumTo].show}")

  val inlined = Inlined(None, Nil, anonFunLambda)
  val inlinedExpr = inlined.asExprOf[EnumFrom => EnumTo]
  log.trace(s"$logPrefix ${inlinedExpr.show}")
  inlinedExpr



/*
AST of tuple (TestEnum11.TestEnumValue3, TestEnum12.TestEnumValue4)
Inlined(
  EmptyTree,
  List(),
  Apply(
    TypeApply(
      Select(Ident(Tuple2),apply),
      List(
        TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum11)],
        TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum12)]
      )
    ),
    List(
      Select(Ident(TestEnum11),TestEnumValue3),
      Select(Ident(TestEnum12),TestEnumValue4)
    )
  )
)

or

Inlined(
  EmptyTree,
  List(),
  Typed(
    SeqLiteral(
      List(
        Apply(
          TypeApply(
            Select(
              Ident(Tuple2),apply),
              List(
                TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum11)],
                TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum12)]
              )
            ),
            List(
              Select(Ident(TestEnum11),TestEnumValue3),
              Select(Ident(TestEnum12),TestEnumValue4)
            )
          )
        ),
        TypeTree[
          AppliedType(
            TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Tuple2),
            List(
              TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum11),
              TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum12)
            )
          )
        ]),
        TypeTree[
          AppliedType(
            TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class <repeated>),
            List(
              AppliedType(
                TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Tuple2),
                List(
                  TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum11),
                  TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum12)
                )
              )
            )
          )
        ]
  )
)
*/

