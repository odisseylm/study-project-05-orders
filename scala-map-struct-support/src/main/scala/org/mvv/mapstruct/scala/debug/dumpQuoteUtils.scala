package org.mvv.mapstruct.scala.debug

import scala.quoted.*
//
import org.mvv.mapstruct.scala.{ unwrapOption, getByReflection }


extension (el: Any)
  def isImplClass(className: String): Boolean =
    org.mvv.mapstruct.scala.isImplClass(el)(className)
  def isOneOfImplClasses(className: String, otherClassNames: String*): Boolean =
    org.mvv.mapstruct.scala.isOneOfImplClasses(el)(className, otherClassNames*)


extension (using quotes: Quotes)(el: quotes.reflect.Tree)

  def toSymbol: Option[quotes.reflect.Symbol] = org.mvv.mapstruct.scala.toSymbol(el)
  //def toTypRepr: Option[quotes.reflect.TypeRepr] = org.mvv.mapstruct.scala.toTypRepr(el)
  def toTypRepr: Option[quotes.reflect.TypeRepr] =
    import quotes.reflect.*
    el match
      case t if t.isTerm => Option(t.asInstanceOf[Term].tpe)
      //case tr if tr.isTermRef => Option(TermRef.unapply(tr.asInstanceOf[TermRef])._1)
      case tt if tt.isTypeTree => Option(tt.asInstanceOf[TypeTree].tpe)
      case wtt if wtt.isWildcardTypeTree => Option(wtt.asInstanceOf[WildcardTypeTree].tpe)
      // ClassOfConstant // No need
      case _ => None


  def isTerm: Boolean = org.mvv.mapstruct.scala.isTerm(el)

  def isNoSymbol: Boolean = org.mvv.mapstruct.scala.isNoSymbol(el)

  def isClassDef: Boolean = org.mvv.mapstruct.scala.isClassDef(el)
  def isValDef: Boolean = org.mvv.mapstruct.scala.isValDef(el)
  def isDefDef: Boolean = org.mvv.mapstruct.scala.isDefDef(el)
  def isTypeDef: Boolean = org.mvv.mapstruct.scala.isTypeDef(el)
  def isDefinition: Boolean = org.mvv.mapstruct.scala.isDefinition(el)
  def isTemplate: Boolean = org.mvv.mapstruct.scala.isTemplate(el)
  def isInferredTypeTree: Boolean = el.getClass.nn.getSimpleName.nn == "InferredTypeTree"

  def isPackageClause: Boolean = org.mvv.mapstruct.scala.isPackageDef(el) && el.isImplClass("PackageClause")
  def isImport: Boolean = org.mvv.mapstruct.scala.isImport(el)
  def isExport: Boolean =
    if !el.isValDef then return false
    el.toSymbol.map(_.name == "<export>").getOrElse(false) || el.isImplClass("Export")

  def isWildcard: Boolean = el.isImplClass("Wildcard")
  def isIdent: Boolean = el.isImplClass("Ident")
  def isSelect: Boolean = el.isImplClass("Select")
  def isRef: Boolean = el.isImplClass("Ref")

  def isThis: Boolean = el.isImplClass("This")
  def isNew: Boolean = el.isImplClass("New")
  def isNamedArg: Boolean = el.isImplClass("NamedArg")
  def isApply: Boolean = el.isImplClass("Apply")
  def isTypeApply: Boolean = el.isImplClass("TypeApply")
  def isSuper: Boolean = el.isImplClass("Super")
  def isTyped: Boolean = el.isImplClass("Typed")

  def isAssign: Boolean = el.isImplClass("Assign")
  def isBlock: Boolean = el.isImplClass("Block")
  def isClosure: Boolean = el.isImplClass("Closure")
  // not tested, probably is incorrect
  def isLambda: Boolean = el.isImplClass("Lambda")

  def isSummonFrom: Boolean = el.isImplClass("SummonFrom")
  def isRepeated: Boolean = el.isImplClass("Repeated")

  def isIf: Boolean = org.mvv.mapstruct.scala.isIf(el)
  def isMatch: Boolean = org.mvv.mapstruct.scala.isMatch(el)
  def isWhile: Boolean = org.mvv.mapstruct.scala.isWhile(el)
  def isTry: Boolean = org.mvv.mapstruct.scala.isTry(el)

  def isLiteral: Boolean = org.mvv.mapstruct.scala.isLiteral(el)

  def isReturn: Boolean = el.isTerm && el.isImplClass("Return")
  def isInlined: Boolean = el.isTerm && el.isImplClass("Inlined")
  def isSelectOuter: Boolean = el.isTerm && el.isImplClass("SelectOuter")
  def isTypedOrTest: Boolean = el.isTerm && el.isImplClass("TypedOrTest")
  def isInferred: Boolean = el.isTerm && el.isImplClass("Inferred")
  def isTypeIdent: Boolean = el.isTerm && el.isImplClass("TypeIdent")
  def isTypeSelect: Boolean = el.isTerm && el.isImplClass("TypeSelect")
  def isTypeProjection: Boolean = el.isTerm && el.isImplClass("TypeProjection")
  def isSingleton: Boolean = el.isTerm && el.isImplClass("Singleton")
    || el.toTypRepr .map(_.isSingleton) .getOrElse(false)
  def isRefined: Boolean = el.isTerm && el.isImplClass("Refined")
  def isApplied: Boolean = el.isTerm && el.isImplClass("Applied")
  def isAnnotated: Boolean = el.isTerm && el.isImplClass("Annotated")
  def isMatchTypeTree: Boolean = el.isTerm && el.isImplClass("MatchTypeTree")
  def isByName: Boolean = el.isTerm && el.isImplClass("ByName")
  def isLambdaTypeTree: Boolean = el.isTerm && el.isImplClass("LambdaTypeTree")
  def isTypeBind: Boolean = el.isTerm && el.isImplClass("TypeBind")
  def isTypeBlock: Boolean = el.isTerm && el.isImplClass("TypeBlock")
  def isTypeTree: Boolean = el.isTerm && el.isImplClass("TypeTree")
  def isTypeBoundsTree: Boolean = el.isTerm && el.isImplClass("TypeBoundsTree")
  def isWildcardTypeTree: Boolean = el.isTerm && el.isImplClass("Try")
  def isCaseDef: Boolean = el.isTerm && el.isImplClass("CaseDef")
  def isTypeCaseDef: Boolean = el.isTerm && el.isImplClass("TypeCaseDef")
  def isBind: Boolean = el.isTerm && el.isImplClass("Bind")
  def isUnapply: Boolean = el.isTerm && el.isImplClass("Unapply")
  def isAlternatives: Boolean = el.isTerm && el.isImplClass("isAlternatives")
  def isStatement: Boolean = el.isTerm && el.isImplClass("Statement")


extension (el: AnyRef)
  def isTermParamClause: Boolean = el.isImplClass("TermParamClause")
  def isTypeParamClause: Boolean = el.isImplClass("TypeParamClause")
  def isParamClause: Boolean = el.isImplClass("ParamClause")
  def isRenameSelector: Boolean = el.isImplClass("RenameSelector")
  def isOmitSelector: Boolean = el.isImplClass("OmitSelector")
  def isGivenSelector: Boolean = el.isImplClass("GivenSelector")
  def isSelector: Boolean = el.isImplClass("Selector")


extension (using quotes: Quotes)(el: quotes.reflect.TypeRepr)
  def isConstantType: Boolean = el.isImplClass("ConstantType")

  def isTermRef: Boolean =
    // It is unclear how to implement this properly...
    val isTermRef = el.isImplClass("TermRef")
      || (try unwrapOption(getByReflection(el, "isTermRef")).asInstanceOf[Boolean]
    catch case _: Exception => false)
    isTermRef

  def isTypeRef: Boolean = el.isImplClass("TypeRef") // TODO: try to impl better

  def isNamedType: Boolean = el.isImplClass("NamedType")
  def isSuperType: Boolean = el.isImplClass("SuperType")
  def isRefinement: Boolean = el.isImplClass("Refinement") || el.typeSymbol.isRefinementClass
  def isAppliedType: Boolean = el.isImplClass("AppliedType")
  def isAnnotatedType: Boolean = el.isImplClass("AnnotatedType")
  def isAndOrType: Boolean = el.isImplClass("AndOrType")
  def isAndType: Boolean = el.isImplClass("AndType")
  def isOrType: Boolean = el.isImplClass("OrType")
  def isMatchType: Boolean = el.isImplClass("MatchType")
  def isByNameType: Boolean = el.isImplClass("ByNameType")
  def isParamRef: Boolean = el.isImplClass("ParamRef")
  def isThisType: Boolean = el.isImplClass("ThisType")
  def isRecursiveThis: Boolean = el.isImplClass("RecursiveThis")
  def isRecursiveType: Boolean = el.isImplClass("RecursiveType")
  def isMethodType: Boolean = el.isImplClass("MethodType")
  def isPolyType: Boolean = el.isImplClass("PolyType")
  def isTypeLambda: Boolean = el.isImplClass("TypeLambda")
  def isMethodOrPoly: Boolean = el.isImplClass("MethodOrPoly")
  def isLambdaType: Boolean = el.isImplClass("LambdaType")
  def isMatchCase: Boolean = el.isImplClass("MatchCase")
  def isTypeBounds: Boolean = el.isImplClass("TypeBounds")
  def isNoPrefix: Boolean = el.isImplClass("NoPrefix") // TODO: try better
  def isTypeRepr: Boolean = el.isImplClass("TypeRepr")


extension (using quotes: Quotes)(el: AnyRef)
  def isBooleanConstant: Boolean = el.isImplClass("BooleanConstant")
  def isByteConstant: Boolean = el.isImplClass("ByteConstant")
  def isShortConstant: Boolean = el.isImplClass("ShortConstant")
  def isIntConstant: Boolean = el.isImplClass("IntConstant")
  def isLongConstant: Boolean = el.isImplClass("LongConstant")
  def isFloatConstant: Boolean = el.isImplClass("FloatConstant")
  def isDoubleConstant: Boolean = el.isImplClass("DoubleConstant")
  def isCharConstant: Boolean = el.isImplClass("CharConstant")
  def isStringConstant: Boolean = el.isImplClass("StringConstant")
  def isUnitConstant: Boolean = el.isImplClass("UnitConstant")
  def isNullConstant: Boolean = el.isImplClass("NullConstant")
  def isClassOfConstant: Boolean = el.isImplClass("ClassOfConstant")
  def isConstant: Boolean = org.mvv.mapstruct.scala.isConstant(el)

  def isImplicitSearchSuccess: Boolean = el.isImplClass("ImplicitSearchSuccess")
  def isDivergingImplicit: Boolean = el.isImplClass("DivergingImplicit")
  def isNoMatchingImplicits: Boolean = el.isImplClass("NoMatchingImplicits")
  def isAmbiguousImplicits: Boolean = el.isImplClass("AmbiguousImplicits")
  def isImplicitSearchFailure: Boolean = el.isImplClass("ImplicitSearchFailure")
  def isImplicitSearchResult: Boolean = el.isImplClass("ImplicitSearchResult")

  def isSymbol: Boolean = el.isImplClass("Symbol") // TODO: ???

  def isFlags: Boolean = el.isImplClass("Flags")
  def isPosition: Boolean = el.isImplClass("Position")
  def isSourceFile: Boolean = el.isImplClass("SourceFile")
