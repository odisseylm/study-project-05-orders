package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.Enum as ScalaEnum
//
import org.mvv.scala.mapstruct.{ Logger, lastAfter, isOneOf, getByReflection, unwrapOption }
// for debug only
import org.mvv.scala.mapstruct.debug.dump.{ isImplClass, activeFlags, activeFlagEntries, dumpSymbol }
import org.mvv.scala.mapstruct.debug.printFields


private val log: Logger = Logger("org.mvv.scala.mapstruct.mappers.enumMappers")


/*
Parameters may only be:
 * Quoted parameters or fields
 * Literal values of primitive types
 * References to `inline val`s
*/

inline def enumMappingFunc[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum](): EnumFrom => EnumTo =
  ${ enumMappingFuncImpl[EnumFrom, EnumTo]( '{ SelectEnumMode.ByEnumFullClassName }, '{ Nil } ) }



//noinspection ScalaUnusedSymbol
inline def enumMappingFunc[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline selectEnumMode: SelectEnumMode): EnumFrom => EnumTo =
  ${ enumMappingFuncImpl[EnumFrom, EnumTo]( 'selectEnumMode, '{ Nil } ) }



//noinspection ScalaUnusedSymbol
inline def enumMappingFunc[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline selectEnumMode: SelectEnumMode, inline customMappings: (EnumFrom, EnumTo)*): EnumFrom => EnumTo =
  ${ enumMappingFuncImpl[EnumFrom, EnumTo]( 'selectEnumMode, 'customMappings ) }



def enumMappingFuncImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (selectEnumModeExpr: Expr[SelectEnumMode], customMappings: Expr[Seq[(EnumFrom, EnumTo)]])
  (using quotes: Quotes)(using etFrom: Type[EnumFrom])(using etTo: Type[EnumTo]):
    Expr[EnumFrom => EnumTo] =

  import quotes.reflect.*

  // unfortunately 'selectEnumModeExpr.valueOrAbort' does not work (for complex types).
  val selectEnumMode = selectEnumModeExpr match
    case sm if sm.matches( '{ SelectEnumMode.ByEnumFullClassName } ) => SelectEnumMode.ByEnumFullClassName
    case sm if sm.matches( '{ SelectEnumMode.ByEnumClassThisType } ) => SelectEnumMode.ByEnumClassThisType
    case other => report.errorAndAbort(s"Unexpected/unparseable selectEnumMode [$other].")

  val enumFromClassName: String = Type.show[EnumFrom]
  val enumToClassName:   String = Type.show[EnumTo]

  val logPrefix = s"enumMappingFuncImpl [ $enumFromClassName => $enumToClassName ], "


  def enumValues[EnumType <: ScalaEnum](using Type[EnumType]): List[String] =
    val classSymbol: Symbol = Symbol.classSymbol(Type.show[EnumType]) // Symbol.requiredClass(typeNameStr)
    val children: List[Symbol] = classSymbol.children
    // maybe with complex enums we need to filter out non-enum items
    val enumNames: List[String] = children.map(_.name)
    enumNames

  def enumValue[EnumType <: ScalaEnum](using Type[EnumType])(enumValue: String): Term =
    selectEnumMode match
      case SelectEnumMode.ByEnumFullClassName => enumValueUsingFullClassName[EnumType](enumValue)
      case SelectEnumMode.ByEnumClassThisType => enumValueUsingSimpleClassNameAndEnumClassThisScope[EnumType](enumValue)

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

  val unexpectedByNameEnumFromValues: List[String] = enumFromValues diff enumToValues
  val unexpectedByNameEnumToValues:   List[String] = enumToValues   diff enumFromValues

  log.trace(s"$logPrefix customMappings: ${customMappings.asTerm}")

  val customMappingAsEnumNames: List[(String, String)] = parseCustomEnumMappingTuplesExpr[EnumFrom, EnumTo](customMappings)
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
      val selectFrom = enumValue[EnumFrom](enumValueFromLabel)
      log.trace(s"$logPrefix selectFrom: $selectFrom")

      val selectTo = enumValue[EnumTo](enumValueToLabel)
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



def enumValueUsingFullClassName[T <: ScalaEnum](using quotes: Quotes)(using enumType: Type[T])(enumValueName: String): quotes.reflect.Term =
  import quotes.reflect.*

  // It does not work... as usual :-(
  //val typeRepr: TypeRepr = TypeRepr.of[T]
  //val classSymbol: Symbol = typeRepr.classSymbol.get // typeRepr.typeSymbol
  //return Select.unique(Ident(classSymbol.termRef), enumValueName)

  // It also does not work
  //val typeRepr: TypeRepr = TypeRepr.of[T]
  //return Select.unique(Ref.term(typeRepr.termSymbol.termRef), enumValueName)

  val fullClassName: String = TypeRepr.of[T].widen.show

  val parts: List[String] = fullClassName.split('.').toList
  require(parts.nonEmpty, s"Invalid enum class [$fullClassName].")

  if parts.sizeIs == 1 then
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
  val enumValueSelect = Select.unique(resultingFullClassNameSelect, enumValueName)
  enumValueSelect



def enumValueUsingSimpleClassNameAndEnumClassThisScope[T <: ScalaEnum]
  (using quotes: Quotes)(using enumType: Type[T])
  (enumValueName: String): quotes.reflect.Term =

  import quotes.reflect.{ Symbol, TypeRepr, TermRef, Ident, Select }

  val typeRepr: TypeRepr = TypeRepr.of[T]
  val classSymbol: Symbol = typeRepr.typeSymbol // typeRepr.classSymbol.get

  val scopeTypRepr: TypeRepr = findClassThisScopeTypeRepr(classSymbol).get
  val fullEnumClassName = typeRepr.show
  val simpleEnumClassName = fullEnumClassName.lastAfter('.').getOrElse(fullEnumClassName)
  val classTerm = TermRef(scopeTypRepr, simpleEnumClassName)
  val enumValueSelect = Select.unique(Ident(classTerm), enumValueName)
  enumValueSelect



def findClassThisScopeTypeRepr(using quotes: Quotes)(symbol: quotes.reflect.Symbol): Option[quotes.reflect.TypeRepr] =
  import quotes.reflect.*

  val typeRepr : Option[TypeRepr] = symbol match
    case td if td.isTypeDef || td.isClassDef =>
      val thisTypeRepr: TypeRepr = TypeRef.unapply(symbol.typeRef)._1 // both work ok
      //val thisTypeRepr: TypeRepr = TermRef.unapply(symbol.termRef)._1 // both work ok
      Option(thisTypeRepr)

    case other =>
      log.warn(s"findCurrentScopeTypeRepr: Unexpected flow $other.")
      None

  typeRepr


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

private def parseCustomEnumMappingTuplesExpr[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using quotes: Quotes)(using Type[EnumFrom])(using Type[EnumTo])
  (inlinedExpr: Expr[Seq[(EnumFrom, EnumTo)]]): List[(String, String)] =
  import quotes.reflect.asTerm
  parseCustomEnumMappingTuples[EnumFrom, EnumTo](inlinedExpr.asTerm.asInstanceOf[quotes.reflect.Inlined])

private def parseCustomEnumMappingTupleExpr[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using quotes: Quotes)(using Type[EnumFrom])(using Type[EnumTo])
  (inlinedExpr: Expr[(EnumFrom, EnumTo)]): (String, String) =
  import quotes.reflect.asTerm
  parseCustomEnumMappingTuples[EnumFrom, EnumTo](inlinedExpr.asTerm.asInstanceOf[quotes.reflect.Inlined]).head



private def parseCustomEnumMappingTuples[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using quotes: Quotes)(using Type[EnumFrom])(using Type[EnumTo])
  (inlined: quotes.reflect.Inlined): List[(String, String)] =
  import quotes.reflect.*

  // TODO: impl
  // val inlinedCall: Option[Tree] = inlined.call
  //require(inlinedCall.isEmpty || inlinedCall.get == EmptyTree, "Expected only simple tuple expression.")

  val bindings: List[Definition] = inlined.bindings
  require(bindings.isEmpty, "Expected only simple tuple expression.")

  val body: Term = inlined.body
  val bodyTypeClassName = typeReprFullClassName(body.tpe)

  def getElementsFromTyped(el: Tree): List[Tree] =
    // Typed ( SeqLiteral ( List(
    require(el.isTyped, s"Typed is expected but was $el.")
    getElements(el.asInstanceOf[Typed].expr)


  val elements: List[Tree] = bodyTypeClassName match
    case "Nil" | "scala.Nil" | "scala.collection.immutable.Nil" => Nil
    case "Tuple2" | "scala.Tuple2" => List(body)
    case "_*" | "<repeated>" | "scala.<repeated>" => getElementsFromTyped(body)
    case "List" | "scala.collection.immutable.List" =>
      require(body.isApply, s"Unexpected List format (creating list using :: is not supported, only simple format List(...) is supported)")
      val listApplyArgs: List[Term] = body.asInstanceOf[Apply].args
      require(listApplyArgs.sizeIs == 1) // List.apply has ONE repeated param
      getElementsFromTyped(listApplyArgs.head)
    case other => throw IllegalStateException(s"Unexpected type of body $other ( $body ).")

  val customEnumMappingTuples = elements.map(el =>
    require(el.isApply)
    parseApplyWithTypeApplyCustomEnumMappingTuple[EnumFrom, EnumTo](el.asInstanceOf[Apply]))
  customEnumMappingTuples


// probably not ideal solution
def typeReprFullClassName(using quotes: Quotes)(typeRepr: quotes.reflect.TypeRepr): String =
  val rawClassFullName = typeRepr.classSymbol.map(_.fullName.stripSuffix("$"))
    .getOrElse(typeRepr.show)
  rawClassFullName

private def getElements(using quotes: Quotes)(tree: quotes.reflect.Tree): List[quotes.reflect.Tree] =
  import quotes.reflect.Tree
  tree match
    case el if el.isSeqLiteral => getByReflection(el, "elems", "elements", "items").unwrapOption.asInstanceOf[List[Tree]]
    case other => throw IllegalStateException(s"Getting elements from ${other.getClass.nn.getName} is not supported yet.")


private def parseApplyWithTypeApplyCustomEnumMappingTuple[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using quotes: Quotes)(using Type[EnumFrom])(using Type[EnumTo])
  (applyWithTypeApply: quotes.reflect.Apply): (String, String) =

  import quotes.reflect.*

  val logPrefix = s"parseApplyWithTypeApplyCustomEnumMappingTuple [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "

  val bodyAsApply: Apply = applyWithTypeApply
  val bodyApplyFun: Term = bodyAsApply.fun
  val bodyApplyArgs: List[Term] = bodyAsApply.args

  require(bodyApplyFun.isTypeApply)
  val typeApply: TypeApply = bodyApplyFun.asInstanceOf[TypeApply]
  val typeApplySelect: Select = typeApply.fun.asInstanceOf[Select]
  val typeApplyClassName = getTypeApplyClassName(typeApplySelect)

  val errorMsgTuple2TypeIsExpected = "Tuple2 is expected."
  require(typeApplyClassName.isOneOf("Tuple2", "scala.Tuple2"), errorMsgTuple2TypeIsExpected)

  val typeApplyArgs: List[TypeTree] = typeApply.args
  require(typeApplyArgs.sizeIs == 2, errorMsgTuple2TypeIsExpected)
  val typeRepr1 = typeApplyArgs.head.tpe
  val typeRepr2 = typeApplyArgs.tail.head.tpe

  require(typeRepr1 == TypeRepr.of[EnumFrom])
  require(typeRepr2 == TypeRepr.of[EnumTo])

  require(bodyApplyArgs.sizeIs == 2, errorMsgTuple2TypeIsExpected)
  val enumValueNames = (extractSimpleName(bodyApplyArgs.head), extractSimpleName(bodyApplyArgs.tail.head))
  log.trace(s"$logPrefix enumValueNames: $enumValueNames")
  enumValueNames



//noinspection ScalaUnusedSymbol // TODO: move to other class/file
extension (using quotes: Quotes)(el: quotes.reflect.Tree)
  private def isTyped: Boolean = el.isImplClass("Typed")
  private def isApply: Boolean = el.isImplClass("Apply")
  private def isTypeApply: Boolean = el.isImplClass("TypeApply")
  private def isSeqLiteral: Boolean = el.isImplClass("SeqLiteral")



private def getTypeApplyClassName(using quotes: Quotes)(typeApply: quotes.reflect.Select): String =
  import quotes.reflect.TypeRepr
  val tpe: TypeRepr = typeApply.tpe
  val qualifierTpe: TypeRepr = typeApply.qualifier.tpe
  val resultingTpe = if tpe.classSymbol.isDefined then tpe else qualifierTpe
  val resultingClassName = resultingTpe.show
  resultingClassName



// Select(Ident(TestEnum1),TestEnumValue4)
// Select(Select(Select(Select(Select(Select(Select(Ident(com),mvv),scala),temp),tests),macros2),TestEnum1)
//
private def extractSimpleName(using quotes: Quotes)(tree: quotes.reflect.Tree): String =
  import quotes.reflect.*
  val rawName: String = tree.symbol.name
  rawName.lastAfter('.').getOrElse(rawName)



/*
// Select(Ident(TestEnum1),TestEnumValue4)
// Select(Select(Select(Select(Select(Select(Select(Ident(com),mvv),scala),temp),tests),macros2),TestEnum1)
//
private def extractName(using quotes: Quotes)(term: quotes.reflect.Tree): String =
  import quotes.reflect.*
  term match
    case el if el.isBind  => el.asInstanceOf[Bind].name
    case el if el.isIdent  => el.asInstanceOf[Ident].name
    case el if el.isTypeIdent => el.asInstanceOf[TypeIdent].name
    case el if el.isSelect => el.asInstanceOf[Select].name
    case el if el.isTypeSelect => el.asInstanceOf[TypeSelect].name
    case el if el.isNamedArg => el.asInstanceOf[NamedArg].name
    case el if el.isSelectOuter => el.asInstanceOf[SelectOuter].name
    case el if el.isSimpleSelector => el.asInstanceOf[SimpleSelector].name
    case el if el.isOmitSelector => el.asInstanceOf[OmitSelector].name
    case el if el.isTypeProjection => el.asInstanceOf[TypeProjection].name
    case el if el.isTypeBind => el.asInstanceOf[TypeBind].name
    case el if el.isNamedType => el.asInstanceOf[NamedType].name
    case el if el.isTypeProjection => el.asInstanceOf[TypeProjection].name
    case el if el.isSymbol => el.asInstanceOf[Symbol].name
    case el if el.isRefinement => el.asInstanceOf[Refinement].name
    case el if el.isDefinition => el.asInstanceOf[Definition].name
*/


//noinspection ScalaUnusedSymbol
//----------------------------------------------------------------------------------------------------------------------
inline def _internalTestCustomMappingsAsRepeatedParams[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline customMappings: (EnumFrom, EnumTo)*): Any =
  ${ _internalTestCustomMappingsAsRepeatedParamsImpl[EnumFrom, EnumTo]( 'customMappings ) }

def _internalTestCustomMappingsAsRepeatedParamsImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (customMappings: Expr[Seq[(EnumFrom, EnumTo)]])
  (using quotes: Quotes)(using etFrom: Type[EnumFrom])(using etTo: Type[EnumTo]): Expr[Any] =

  import quotes.reflect.*

  val logPrefix = s"_internalTestCustomMappingsAsRepeatedParamsImpl [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "
  log.trace(s"$logPrefix customMappingsRepetaed: ${customMappings.asTerm}")

  val customMappingAsEnumNames: List[(String, String)] = parseCustomEnumMappingTuplesExpr[EnumFrom, EnumTo](customMappings)
  log.info(s"$logPrefix customMappingAsEnumNames: $customMappingAsEnumNames")
  '{}


//----------------------------------------------------------------------------------------------------------------------
inline def _internalTestCustomMappingsAsListParam[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline customMappings: List[(EnumFrom, EnumTo)]): Any =
  ${ _internalTestCustomMappingsAsListParamImpl[EnumFrom, EnumTo]( 'customMappings ) }

def _internalTestCustomMappingsAsListParamImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (customMappings: Expr[Seq[(EnumFrom, EnumTo)]])
  (using quotes: Quotes)(using etFrom: Type[EnumFrom])(using etTo: Type[EnumTo]): Expr[Any] =

  import quotes.reflect.*

  val logPrefix = s"_internalTestCustomMappingsAsListParamsImpl [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "
  log.trace(s"$logPrefix customMappingsList: ${customMappings.asTerm}")

  val customMappingAsEnumNames: List[(String, String)] = parseCustomEnumMappingTuplesExpr[EnumFrom, EnumTo](customMappings)
  log.info(s"$logPrefix customMappingAsEnumNames: $customMappingAsEnumNames")
  '{}

//----------------------------------------------------------------------------------------------------------------------
inline def _internalTestCustomMappingsAsSingleParam[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline customMapping: (EnumFrom, EnumTo)): Any =
  ${ _internalTestCustomMappingsAsSingleParamImpl[EnumFrom, EnumTo]( 'customMapping ) }

def _internalTestCustomMappingsAsSingleParamImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (customMapping: Expr[(EnumFrom, EnumTo)])
  (using quotes: Quotes)(using etFrom: Type[EnumFrom])(using etTo: Type[EnumTo]): Expr[Any] =

  import quotes.reflect.*

  val logPrefix = s"_internalTestCustomMappingsAsSingleParamsImpl [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "
  log.trace(s"$logPrefix customMappingsSingleParam: ${customMapping.asTerm}")

  val customMappingAsEnumNames: (String, String) = parseCustomEnumMappingTupleExpr[EnumFrom, EnumTo](customMapping)
  log.info(s"$logPrefix customMappingAsEnumNames: $customMappingAsEnumNames")
  '{}

