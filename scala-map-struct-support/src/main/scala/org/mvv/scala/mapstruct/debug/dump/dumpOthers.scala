package org.mvv.scala.mapstruct.debug.dump

import scala.quoted.*


// overloading does not work?!
def dumpTree1(using quotes: Quotes)(tree: quotes.reflect.Tree): Unit =
  val str = StringBuilder()
  dumpTree(tree, str, 2)
  print(str)


def dumpTree(using quotes: Quotes)(tree: quotes.reflect.Tree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*

  val nextPadLength = padLength + indentPerLevel
  //val padStr = " ".repeat(padLength)
  //val padChildStr = " ".repeat(nextPadLength)
  tree match
    // PackageClause <: Tree
    case el if el.isPackageClause =>
      // WARN it may not work !!!
      dumpPackageClause(el.asInstanceOf[PackageClause], str, nextPadLength)

    // Statement <: Tree
    // Import <: Statement
    case el if el.isImport => dumpImport(el.asInstanceOf[Import], str, nextPadLength)
    // Export <: Statement
    case el if el.isExport => dumpExport(el.asInstanceOf[Export], str, nextPadLength)

    // Definition <: Statement <: Tree
    // Template is not present now in API??..
    case el if el.isTemplate => dumpTemplate(el, str, nextPadLength)
    // ClassDef <: Definition
    case el if el.isClassDef =>
      el match
        case el if el.isInferredTypeTree => dumpInferredTypeTree(el.asInstanceOf[TypeTree], str, nextPadLength)
        //case el if el.isMemberDef => dumpMemberDef(el.asInstanceOf[TypeTree], str, nextPadLength)
        case el if el.isIdent => dumpIdent(el.asInstanceOf[Term], str, nextPadLength)
        case _ => dumpClassDef(el.asInstanceOf[ClassDef], str, nextPadLength)
    // DefDef <: Definition
    case el if el.isDefDef =>
      el match
        case it if it.isSelect => dumpSelect(it.asInstanceOf[Select], str, nextPadLength)
        case it if it.isIdent => dumpIdent(it.asInstanceOf[Term], str, nextPadLength)
        case it if it.isApply => dumpApply(it.asInstanceOf[Apply], str, nextPadLength)
        case it if it.isTypeApply => dumpTypeApply(it.asInstanceOf[TypeApply], str, nextPadLength)
        //case el if el.isMemberDef => dumpMemberDef(el.asInstanceOf[TypeTree], str, nextPadLength)
        case _ =>
          try dumpDefDef(el.asInstanceOf[DefDef], str, nextPadLength)
          catch case ex: Exception =>
            log.error(s"Error of DefDef parsing from $el", ex)
            throw ex
    // about ValDef
    //case el if el.isInferredTypeTree => dumpInferredTypeTree(el.asInstanceOf[TypeTree], str, nextPadLength)
    // ValDef <: Definition
    case el if el.isValDef =>
      el match
        case it if it.isInferredTypeTree => dumpInferredTypeTree(it.asInstanceOf[TypeTree], str, nextPadLength)
        case it if it.isSelect => dumpSelect(it.asInstanceOf[Select], str, nextPadLength)
        case it if it.isIdent => dumpIdent(it.asInstanceOf[Term], str, nextPadLength)
        case el if el.isTyped => dumpTyped(el.asInstanceOf[Typed], str, nextPadLength)
        // hm... strange type
        case el if el.isTypeTree => dumpBaseTypeTree(el.asInstanceOf[TypeTree], str, nextPadLength)
        //case el if el.isMemberDef => dumpMemberDef(el.asInstanceOf[TypeTree], str, nextPadLength)
        case _ => dumpValDef(el.asInstanceOf[ValDef], str, nextPadLength)
    // TypeDef <: Definition
    case el if el.isTypeDef => dumpTypeDef(el.asInstanceOf[TypeDef], str, nextPadLength)
    // base Definition = Definition <: Statement <: Tree
    case el if el.isDefinition => dumpDefinition(el.asInstanceOf[Definition], str, nextPadLength)

    // Term <: Statement
    //
    // Ref <: Term
    //
    // Wildcard <: Ident <: Ref
    case el if el.isWildcard => dumpWildcardClause(el.asInstanceOf[Wildcard], str, nextPadLength)
    // Ident <: Ref
    case el if el.isIdent => dumpIdent(el.asInstanceOf[Term], str, nextPadLength)
    // Select <: Ref
    case el if el.isSelect => dumpSelect(el.asInstanceOf[Select], str, nextPadLength)
    // base Ref = Ref <: Term
    case el if el.isRef => dumpRef(el.asInstanceOf[Ref], str, nextPadLength)


    // Literal <: Term
    case el if el.isLiteral => dumpLiteral(el.asInstanceOf[Literal], str, nextPadLength)
    // This <: Term
    case el if el.isThis => dumpThis(el.asInstanceOf[This], str, nextPadLength)
    // New <: Term
    case el if el.isNew => dumpNew(el.asInstanceOf[New], str, nextPadLength)
    // NamedArg <: Term
    case el if el.isNamedArg => dumpNamedArg(el.asInstanceOf[NamedArg], str, nextPadLength)
    // Apply <: Term
    case el if el.isApply => dumpApply(el.asInstanceOf[Apply], str, nextPadLength)
    // TypeApply <: Term
    case el if el.isTypeApply => dumpTypeApply(el.asInstanceOf[TypeApply], str, nextPadLength)
    // Super <: Term
    case el if el.isSuper => dumpSuper(el.asInstanceOf[Super], str, nextPadLength)
    // Typed <: Term & TypedOrTest
    case el if el.isTyped => dumpTyped(el.asInstanceOf[Typed], str, nextPadLength)
    // Assign <: Term
    case el if el.isAssign => dumpAssign(el.asInstanceOf[Assign], str, nextPadLength)
    // Lambda
    case el if el.isLambda => dumpLambda(el.asInstanceOf[Block], str, nextPadLength)
    // Block <: Term
    case el if el.isBlock => dumpBlock(el.asInstanceOf[Block], str, nextPadLength)
    // Closure <: Term
    case el if el.isClosure => dumpClosure(el.asInstanceOf[Closure], str, nextPadLength)
    // Lambda: LambdaModule
    // ?

    // ???
    // T O D O: use lambda
    //case el if el.isLambda => dumpLambda(el.asInstanceOf[Lambda], str, nextPadLength)
    // If <: Term
    case el if el.isIf => dumpIf(el.asInstanceOf[If], str, nextPadLength)
    // Match <: Term
    case el if el.isMatch => dumpMatch(el.asInstanceOf[Match], str, nextPadLength)
    // SummonFrom <: Term
    case el if el.isSummonFrom => dumpSummonFrom(el.asInstanceOf[SummonFrom], str, nextPadLength)
    // Try <: Term
    case el if el.isTry => dumpTry(el.asInstanceOf[Try], str, nextPadLength)
    // Return <: Term
    case el if el.isReturn => dumpReturn(el.asInstanceOf[Return], str, nextPadLength)
    // Repeated <: Term
    case el if el.isRepeated => dumpRepeated(el.asInstanceOf[Repeated], str, nextPadLength)
    // Inlined <: Term
    case el if el.isInlined => dumpInlined(el.asInstanceOf[Inlined], str, nextPadLength)
    // SelectOuter <: Term
    case el if el.isSelectOuter => dumpSelectOuter(el.asInstanceOf[SelectOuter], str, nextPadLength)
    // While <: Term
    case el if el.isWhile => dumpWhile(el.asInstanceOf[While], str, nextPadLength)

    // TypedOrTest <: Tree
    case el if el.isTypedOrTest => dumpTypedOrTest(el.asInstanceOf[TypedOrTest], str, nextPadLength)
    // Inferred <: TypeTree
    case el if el.isInferred => dumpInferred(el.asInstanceOf[Inferred], str, nextPadLength)
    // TypeIdent <: TypeTree
    case el if el.isTypeIdent => dumpTypeIdent(el.asInstanceOf[TypeIdent], str, nextPadLength)
    // TypeSelect <: TypeTree
    case el if el.isTypeSelect => dumpTypeSelect(el.asInstanceOf[TypeSelect], str, nextPadLength)
    // TypeProjection <: TypeTree
    case el if el.isTypeProjection => dumpTypeProjection(el.asInstanceOf[TypeProjection], str, nextPadLength)
    // Singleton <: TypeTree
    case el if el.isSingleton => dumpSingleton(el.asInstanceOf[Singleton], str, nextPadLength)
    // Refined <: TypeTree
    case el if el.isRefined => dumpRefined(el.asInstanceOf[Refined], str, nextPadLength)
    // Applied <: TypeTree
    case el if el.isApplied => dumpApplied(el.asInstanceOf[Applied], str, nextPadLength)
    // Annotated <: TypeTree
    case el if el.isAnnotated => dumpAnnotated(el.asInstanceOf[Annotated], str, nextPadLength)
    // MatchTypeTree <: TypeTree
    case el if el.isMatchTypeTree => dumpMatchTypeTree(el.asInstanceOf[MatchTypeTree], str, nextPadLength)
    // ByName <: TypeTree
    case el if el.isByName => dumpByName(el.asInstanceOf[ByName], str, nextPadLength)
    // LambdaTypeTree <: TypeTree
    case el if el.isLambdaTypeTree => dumpLambdaTypeTree(el.asInstanceOf[LambdaTypeTree], str, nextPadLength)
    // TypeBind <: TypeTree
    case el if el.isTypeBind => dumpTypeBind(el.asInstanceOf[TypeBind], str, nextPadLength)
    // TypeBlock <: TypeTree
    case el if el.isTypeBlock => dumpTypeBlock(el.asInstanceOf[TypeBlock], str, nextPadLength)
    //
    // base TypeTree == TypeTree <: Tree
    case el if el.isTypeTree => dumpBaseTypeTree(el.asInstanceOf[TypeTree], str, nextPadLength)

    // TypeBoundsTree <: Tree
    case el if el.isTypeBoundsTree => dumpTypeBoundsTree(el.asInstanceOf[TypeBoundsTree], str, nextPadLength)
    // WildcardTypeTree <: Tree
    case el if el.isWildcardTypeTree => dumpWildcardTypeTree(el.asInstanceOf[WildcardTypeTree], str, nextPadLength)
    // CaseDef <: Tree
    case el if el.isCaseDef => dumpCaseDef(el.asInstanceOf[CaseDef], str, nextPadLength)
    // TypeCaseDef <: Tree
    case el if el.isTypeCaseDef => dumpTypeCaseDef(el.asInstanceOf[TypeCaseDef], str, nextPadLength)
    // Bind <: Tree
    case el if el.isBind => dumpBind(el.asInstanceOf[Bind], str, nextPadLength)
    // Unapply <: Tree
    case el if el.isUnapply => dumpUnapply(el.asInstanceOf[Unapply], str, nextPadLength)
    // Alternatives <: Tree
    case el if el.isAlternatives => dumpAlternatives(el.asInstanceOf[Alternatives], str, nextPadLength)
    //
    // base Term
    case el if el.isTerm => dumpBaseTerm(el.asInstanceOf[Term], str, nextPadLength)
    // base Statement
    case el if el.isStatement => dumpBaseStatement(el.asInstanceOf[Statement], str, nextPadLength)



// probably it is bad impl because
private def dumpParam(using quotes: Quotes)(param: quotes.reflect.ParamClause, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*

  //noinspection TypeCheckCanBeMatch
  if param.isInstanceOf[List[?]] then
    val paramsList: List[Tree] = param.asInstanceOf[List[Tree]]
    // T O D O: refactor/improve
    paramsList.foreach { pp => dumpTree(pp, str, padLength) }
    { return }

  param match
    // ParamClause <: AnyRef
    // TermParamClause <: ParamClause
    case el if el.isTermParamClause => dumpTermParamClause(el.asInstanceOf[TermParamClause], str, padLength)
    // TypeParamClause <: ParamClause
    case el if el.isTypeParamClause => dumpTypeParamClause(el.asInstanceOf[TypeParamClause], str, padLength)
    // base ParamClause = ParamClause <: AnyRef
    case el if el.isParamClause => dumpParamClause(el, str, padLength)
    case other =>
      log.error(s"Unexpected param class ${other.getClass.nn.getName}, $other.")
      throw IllegalThreadStateException(s"Unexpected param class ${other.getClass.nn.getName}.")






// bae TypeRepr = type TypeRepr
private def dumpTypeRepr(using quotes: Quotes)(tree: quotes.reflect.TypeRepr, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*

  val nextPadLength = padLength + indentPerLevel
  //val padStr = " ".repeat(padLength)
  //val padChildStr = " ".repeat(nextPadLength)
  tree match
    // type TypeRepr
    // ConstantType <: TypeRepr
    case el if el.isConstantType => dumpConstantType(el.asInstanceOf[ConstantType], str, nextPadLength)
    // NamedType <: TypeRepr
    // TermRef <: NamedType <: TypeRepr
    case el if el.isTermRef => dumpTermRef(el.asInstanceOf[TermRef], str, nextPadLength)
    // TypeRef <: NamedType <: TypeRepr
    case el if el.isTypeRef => dumpTypeRef(el.asInstanceOf[TypeRef], str, nextPadLength)
    // base NamedType = NamedType <: TypeRepr
    case el if el.isNamedType => dumpNamedType(el.asInstanceOf[NamedType], str, nextPadLength)

    // SuperType <: TypeRepr
    case el if el.isSuperType => dumpSuperType(el.asInstanceOf[SuperType], str, nextPadLength)
    // Refinement <: TypeRepr
    case el if el.isRefinement => dumpRefinement(el.asInstanceOf[Refinement], str, nextPadLength)
    // AppliedType <: TypeRepr
    case el if el.isAppliedType => dumpAppliedType(el.asInstanceOf[AppliedType], str, nextPadLength)
    // AnnotatedType <: TypeRepr
    case el if el.isAnnotatedType => dumpAnnotatedType(el.asInstanceOf[AnnotatedType], str, nextPadLength)
    // AndOrType <: TypeRepr
    case el if el.isAndOrType => dumpAndOrType(el.asInstanceOf[AndOrType], str, nextPadLength)
    // AndType <: AndOrType
    case el if el.isAndType => dumpAndType(el.asInstanceOf[AndType], str, nextPadLength)
    // OrType <: AndOrType
    case el if el.isOrType => dumpOrType(el.asInstanceOf[OrType], str, nextPadLength)
    // MatchType <: TypeRepr
    case el if el.isMatchType => dumpMatchType(el.asInstanceOf[MatchType], str, nextPadLength)
    // ByNameType <: TypeRepr
    case el if el.isByNameType => dumpByNameType(el.asInstanceOf[ByNameType], str, nextPadLength)
    // ParamRef <: TypeRepr
    case el if el.isParamRef => dumpParamRef(el.asInstanceOf[ParamRef], str, nextPadLength)
    // ThisType <: TypeRepr
    case el if el.isThisType => dumpThisType(el.asInstanceOf[ThisType], str, nextPadLength)
    // RecursiveThis <: TypeRepr
    case el if el.isRecursiveThis => dumpRecursiveThis(el.asInstanceOf[RecursiveThis], str, nextPadLength)
    // RecursiveType <: TypeRepr
    case el if el.isRecursiveType => dumpRecursiveType(el.asInstanceOf[RecursiveType], str, nextPadLength)
    //
    // LambdaType <: TypeRepr
    // MethodOrPoly <: LambdaType
    // MethodType <: MethodOrPoly
    case el if el.isMethodType => dumpMethodType(el.asInstanceOf[MethodType], str, nextPadLength)
    // PolyType <: MethodOrPoly
    case el if el.isPolyType => dumpPolyType(el.asInstanceOf[PolyType], str, nextPadLength)
    // TypeLambda <: LambdaType
    case el if el.isTypeLambda => dumpTypeLambda(el.asInstanceOf[TypeLambda], str, nextPadLength)
    // base MethodOrPoly <: LambdaType
    case el if el.isMethodOrPoly => dumpMethodOrPoly(el.asInstanceOf[MethodOrPoly], str, nextPadLength)
    // base LambdaType = LambdaType <: TypeRepr
    case el if el.isLambdaType => dumpLambdaType(el.asInstanceOf[LambdaType], str, nextPadLength)

    //
    // MatchCase <: TypeRepr
    case el if el.isMatchCase => dumpMatchCase(el.asInstanceOf[MatchCase], str, nextPadLength)
    // TypeBounds <: TypeRepr
    case el if el.isTypeBounds => dumpTypeBounds(el.asInstanceOf[TypeBounds], str, nextPadLength)
    // NoPrefix <: TypeRepr
    case el if el.isNoPrefix => dumpNoPrefix(el.asInstanceOf[NoPrefix], str, nextPadLength)
    // bae TypeRepr = type TypeRepr
    case el if el.isTypeRepr => dumpBaseTypeRepr(el, str, nextPadLength)


// base Constant <: AnyRef
private def dumpConstant(using quotes: Quotes)(constant: quotes.reflect.Constant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  constant match
    // Constant <: AnyRef
    // BooleanConstant <: Constant
    case el if el.isBooleanConstant => dumpBooleanConstant(el.asInstanceOf[BooleanConstant], str, padLength)
    // ByteConstant <: Constant
    case el if el.isByteConstant => dumpByteConstant(el.asInstanceOf[ByteConstant], str, padLength)
    // ShortConstant <: Constant
    case el if el.isShortConstant => dumpShortConstant(el.asInstanceOf[ShortConstant], str, padLength)
    // IntConstant <: Constant
    case el if el.isIntConstant => dumpIntConstant(el.asInstanceOf[IntConstant], str, padLength)
    // LongConstant <: Constant
    case el if el.isLongConstant => dumpLongConstant(el.asInstanceOf[LongConstant], str, padLength)
    // FloatConstant <: Constant
    case el if el.isFloatConstant => dumpFloatConstant(el.asInstanceOf[FloatConstant], str, padLength)
    // DoubleConstant <: Constant
    case el if el.isDoubleConstant => dumpDoubleConstant(el.asInstanceOf[DoubleConstant], str, padLength)
    // CharConstant <: Constant
    case el if el.isCharConstant => dumpCharConstant(el.asInstanceOf[CharConstant], str, padLength)
    // StringConstant <: Constant
    case el if el.isStringConstant => dumpStringConstant(el.asInstanceOf[StringConstant], str, padLength)
    // UnitConstant <: Constant
    case el if el.isUnitConstant => dumpUnitConstant(el.asInstanceOf[UnitConstant], str, padLength)
    // NullConstant <: Constant
    case el if el.isNullConstant => dumpNullConstant(el.asInstanceOf[NullConstant], str, padLength)
    // ClassOfConstant <: Constant
    case el if el.isClassOfConstant => dumpClassOfConstant(el.asInstanceOf[ClassOfConstant], str, padLength)
    // base Constant <: AnyRef
    case el if el.isConstant => dumpBaseConstant(el, str, padLength)
    case other => throw IllegalStateException(s"Unexpected constant [${other.getClass.nn.getName}] [${other.getClass.nn.getSimpleName}] $other")

private def dumpImplicitSearch(using quotes: Quotes)(implicitSearch: AnyRef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  implicitSearch match
    // ImplicitSearchResult <: AnyRef
    // ImplicitSearchSuccess <: ImplicitSearchResult
    case el if el.isImplicitSearchSuccess => dumpImplicitSearchSuccess(el.asInstanceOf[ImplicitSearchSuccess], str, padLength)
    // ImplicitSearchFailure <: ImplicitSearchResult
    // DivergingImplicit <: ImplicitSearchFailure
    case el if el.isDivergingImplicit => dumpDivergingImplicit(el.asInstanceOf[DivergingImplicit], str, padLength)
    // NoMatchingImplicits <: ImplicitSearchFailure
    case el if el.isNoMatchingImplicits => dumpNoMatchingImplicits(el.asInstanceOf[NoMatchingImplicits], str, padLength)
    // AmbiguousImplicits <: ImplicitSearchFailure
    case el if el.isAmbiguousImplicits => dumpAmbiguousImplicits(el.asInstanceOf[AmbiguousImplicits], str, padLength)
    // base ImplicitSearchFailure <: ImplicitSearchResult
    case el if el.isImplicitSearchFailure => dumpImplicitSearchFailure(el.asInstanceOf[ImplicitSearchFailure], str, padLength)
    // base ImplicitSearchResult <: AnyRef
    case el if el.isImplicitSearchResult => dumpImplicitSearchResult(el.asInstanceOf[ImplicitSearchResult], str, padLength)

/*
    // Symbol <: AnyRef
    case el if el.isSymbol => dumpSymbol(el.asInstanceOf[Symbol], str, nextPadLength)

    // type Flags
    case el if el.isFlags => dumpFlags(el.asInstanceOf[Flags], str, nextPadLength)

    // type Position <: AnyRef
    case el if el.isPosition => dumpPosition(el.asInstanceOf[Position], str, nextPadLength)
    // SourceFile <: AnyRef
    case el if el.isSourceFile => dumpSourceFile(el.asInstanceOf[SourceFile], str, nextPadLength)
*/
