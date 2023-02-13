package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// type TypeRepr
// ConstantType <: TypeRepr
def dumpConstantType(using quotes: Quotes)(ct: quotes.reflect.ConstantType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val constant: Constant = ct.constant
  str.addTagName("<ConstantType>", padLength)
    dumpTypeReprImpl(ct, str, padLength)
    str.addChildTagName("<constant>", padLength)
    dumpConstant(constant, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</constant>", padLength)
  str.addTagName("</ConstantType>", padLength)



// NamedType <: TypeRepr
//
// TermRef <: NamedType <: TypeRepr
def dumpTermRef(using quotes: Quotes)(termRef: quotes.reflect.TermRef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  str.addTagName("<TermRef>", padLength)
    dumpNamedTypeImpl(termRef, str, padLength)
  str.addTagName("</TermRef>", padLength)


// TypeRef <: NamedType <: TypeRepr
def dumpTypeRef(using quotes: Quotes)(typeRef: quotes.reflect.TypeRef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val isOpaqueAlias: Boolean = typeRef.isOpaqueAlias
  val translucentSuperType: TypeRepr = typeRef.translucentSuperType

  str.addTagName("<TypeRef>", padLength)
    if isOpaqueAlias then str.addChildTagName("isOpaqueAlias", padLength)
    dumpNamedTypeImpl(typeRef, str, padLength)
    str.addChildTagName("translucentSuperType", translucentSuperType.show, padLength)
  str.addTagName("</TypeRef>", padLength)



// base NamedType = NamedType <: TypeRepr
def dumpNamedType(using quotes: Quotes)(namedType: quotes.reflect.NamedType, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<NamedType>", padLength)
    dumpNamedTypeImpl(namedType, str, padLength)
  str.addTagName("</NamedType>", padLength)

def dumpNamedTypeImpl(using quotes: Quotes)(namedType: quotes.reflect.NamedType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val qualifier: TypeRepr = namedType.qualifier
  val name: String = namedType.name

  dumpTypeReprImpl(namedType, str, padLength)
  str.addChildTagName("qualifier", typeReprToString(qualifier), padLength)
  str.addChildTagName("name", name, padLength)



// SuperType <: TypeRepr
def dumpSuperType(using quotes: Quotes)(superType: quotes.reflect.SuperType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val thistpe: TypeRepr = superType.thistpe
  val supertpe: TypeRepr = superType.supertpe

  str.addTagName("<SuperType>", padLength)
    dumpTypeReprImpl(superType, str, padLength)
    str.addChildTagName("thistpe", typeReprToString(thistpe), padLength)
    str.addChildTagName("supertpe", typeReprToString(supertpe), padLength)
  str.addTagName("</SuperType>", padLength)


// Refinement <: TypeRepr
def dumpRefinement(using quotes: Quotes)(refinement: quotes.reflect.Refinement, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = refinement.name
  val info: TypeRepr = refinement.info
  val parent: TypeRepr = refinement.parent

  str.addTagName("<Refinement>", padLength)
    dumpTypeReprImpl(refinement, str, padLength)
    str.addChildTagName("name", name, padLength)
    str.addChildTagName("info", typeReprToString(info), padLength)
    str.addChildTagName("parent", typeReprToString(parent), padLength)
  str.addTagName("</Refinement>", padLength)



// AppliedType <: TypeRepr
def dumpAppliedType(using quotes: Quotes)(appliedType: quotes.reflect.AppliedType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tycon: TypeRepr = appliedType.tycon
  val args: List[TypeRepr] = appliedType.args

  str.addTagName("<AppliedType>", padLength)
    dumpTypeReprImpl(appliedType, str, padLength)
    str.addChildTagName("tycon", typeReprToString(tycon), padLength)

    str.addChildTagName("<args>", padLength)
      args.foreach(a => dumpTypeRepr(a, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</args>", padLength)
  str.addTagName("</AppliedType>", padLength)



// AnnotatedType <: TypeRepr
def dumpAnnotatedType(using quotes: Quotes)(annotatedType: quotes.reflect.AnnotatedType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val underlying: TypeRepr = annotatedType.underlying
  val annotation: Term = annotatedType.annotation

  str.addTagName("<AppliedType>", padLength)
    dumpTypeReprImpl(annotatedType, str, padLength)
    str.addChildTagName("underlying", typeReprToString(underlying), padLength)

    str.addChildTagName("<annotation>", padLength)
      dumpTree(annotation, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</annotation>", padLength)
  str.addTagName("</AppliedType>", padLength)



// AndOrType <: TypeRepr
def dumpAndOrType(using quotes: Quotes)(andOrType: quotes.reflect.AndOrType, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<AndOrType>", padLength)
    dumpAndOrTypeImpl(andOrType, str, padLength)
  str.addTagName("</AndOrType>", padLength)


// AndOrType <: TypeRepr
private def dumpAndOrTypeImpl(using quotes: Quotes)(andOrType: quotes.reflect.AndOrType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val left: TypeRepr = andOrType.left
  val right: TypeRepr = andOrType.right

  dumpTypeReprImpl(andOrType, str, padLength)
  str.addChildTagName("left", typeReprToString(left), padLength)
  str.addChildTagName("right", typeReprToString(right), padLength)



// AndType <: AndOrType
def dumpAndType(using quotes: Quotes)(andType: quotes.reflect.AndType, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<AndType>", padLength)
    dumpAndOrTypeImpl(andType, str, padLength)
  str.addTagName("</AndType>", padLength)


// OrType <: AndOrType
def dumpOrType(using quotes: Quotes)(orType: quotes.reflect.OrType, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<OrType>", padLength)
    dumpAndOrTypeImpl(orType, str, padLength)
  str.addTagName("</OrType>", padLength)



// MatchType <: TypeRepr
def dumpMatchType(using quotes: Quotes)(matchType: quotes.reflect.MatchType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val bound: TypeRepr = matchType.bound
  val scrutinee: TypeRepr = matchType.scrutinee
  val cases: List[TypeRepr] = matchType.cases

  str.addTagName("<MatchType>", padLength)
    dumpTypeReprImpl(matchType, str, padLength)
    str.addChildTagName("bound", typeReprToString(bound), padLength)
    str.addChildTagName("scrutinee", typeReprToString(scrutinee), padLength)

    str.addChildTagName("<cases>", str, padLength)
      cases.foreach(c => dumpTypeRepr(c, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</cases>", str, padLength)
  str.addTagName("</MatchType>", padLength)



// ByNameType <: TypeRepr
def dumpByNameType(using quotes: Quotes)(byNameType: quotes.reflect.ByNameType, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val underlying: TypeRepr = byNameType.underlying

  str.addTagName("<ByNameType>", padLength)
    dumpTypeReprImpl(byNameType, str, padLength)
    str.addChildTagName("underlying", typeReprToString(underlying), padLength)
  str.addTagName("</ByNameType>", padLength)



// ParamRef <: TypeRepr
def dumpParamRef(using quotes: Quotes)(paramRef: quotes.reflect.ParamRef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val binder: TypeRepr = paramRef.binder
  val paramNum: Int = paramRef.paramNum

  str.addTagName("<ParamRef>", padLength)
    dumpTypeReprImpl(paramRef, str, padLength)
    str.addChildTagName("paramNum", paramNum, padLength)
    str.addChildTagName("binder", typeReprToString(binder), padLength)
  str.addTagName("</ParamRef>", padLength)



// ThisType <: TypeRepr
def dumpThisType(using quotes: Quotes)(el: quotes.reflect.ThisType, str: StringBuilder, padLength: Int): Unit = {}


// RecursiveThis <: TypeRepr
def dumpRecursiveThis(using quotes: Quotes)(el: quotes.reflect.RecursiveThis, str: StringBuilder, padLength: Int): Unit = {}


// RecursiveType <: TypeRepr
def dumpRecursiveType(using quotes: Quotes)(el: quotes.reflect.RecursiveType, str: StringBuilder, padLength: Int): Unit = {}


//
// LambdaType <: TypeRepr
// MethodOrPoly <: LambdaType
// MethodType <: MethodOrPoly
def dumpMethodType(using quotes: Quotes)(el: quotes.reflect.MethodType, str: StringBuilder, padLength: Int): Unit = {}


// PolyType <: MethodOrPoly
def dumpPolyType(using quotes: Quotes)(el: quotes.reflect.PolyType, str: StringBuilder, padLength: Int): Unit = {}


// TypeLambda <: LambdaType
def dumpTypeLambda(using quotes: Quotes)(el: quotes.reflect.TypeLambda, str: StringBuilder, padLength: Int): Unit = {}


// base MethodOrPoly <: LambdaType
def dumpMethodOrPoly(using quotes: Quotes)(el: quotes.reflect.MethodOrPoly, str: StringBuilder, padLength: Int): Unit = {}


// base LambdaType = LambdaType <: TypeRepr
def dumpLambdaType(using quotes: Quotes)(el: quotes.reflect.LambdaType, str: StringBuilder, padLength: Int): Unit = {}


//
// MatchCase <: TypeRepr
def dumpMatchCase(using quotes: Quotes)(el: quotes.reflect.MatchCase, str: StringBuilder, padLength: Int): Unit = {}


// TypeBounds <: TypeRepr
def dumpTypeBounds(using quotes: Quotes)(el: quotes.reflect.TypeBounds, str: StringBuilder, padLength: Int): Unit = {}


// NoPrefix <: TypeRepr
def dumpNoPrefix(using quotes: Quotes)(el: quotes.reflect.NoPrefix, str: StringBuilder, padLength: Int): Unit = {}


// base type TypeRepr
def dumpBaseTypeRepr(using quotes: Quotes)(typeRepr: quotes.reflect.TypeRepr, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<TypeRepr>", padLength)
    dumpTypeReprImpl(typeRepr, str, padLength)
  str.addTagName("</TypeRepr>", padLength)


private def dumpTypeReprImpl(using quotes: Quotes)(typeRepr: quotes.reflect.TypeRepr, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  str.addChildTagName(typeReprToString(typeRepr), padLength)
  // skip
  //val baseClasses: List[Symbol]
  //val baseType(cls: Symbol): TypeRepr



def typeReprToString(using quotes: Quotes)(typeRepr: quotes.reflect.TypeRepr): String =
  import quotes.reflect.*
  val show: String = typeRepr.show
  val dealias: TypeRepr = typeRepr.dealias
  val dealiasShow: String = dealias.show
  val simplified: TypeRepr = typeRepr.simplified
  val simplifiedShow: String = simplified.show
  val widen: TypeRepr = typeRepr.widen
  val widenShow: String = widen.show

  // skip now
  val classSymbol: Option[Symbol] = typeRepr.classSymbol
  val typeSymbol: Symbol = typeRepr.typeSymbol
  val termSymbol: Symbol = typeRepr.termSymbol

  val isSingleton: Boolean = typeRepr.isSingleton
  val isFunctionType: Boolean = typeRepr.isFunctionType
  val isContextFunctionType: Boolean = typeRepr.isContextFunctionType
  val isErasedFunctionType: Boolean = typeRepr.isErasedFunctionType
  val isDependentFunctionType: Boolean = typeRepr.isDependentFunctionType
  val isTupleN: Boolean = typeRepr.isTupleN
  val typeArgs: List[TypeRepr] = typeRepr.typeArgs

  val str = StringBuilder()
  str.append(show)
  if dealiasShow.nonEmptyName && dealiasShow != show then str.append(" dealias: ").append(dealiasShow)
  if simplifiedShow.nonEmptyName && simplifiedShow != show then str.append(" simplified: ").append(simplifiedShow)
  if widenShow.nonEmptyName && dealiasShow != show then str.append(" widen: ").append(widenShow)

  if isSingleton then str.append(" isSingleton")
  if isFunctionType then str.append(" isFunctionType")
  if isContextFunctionType then str.append(" isContextFunctionType")
  if isErasedFunctionType then str.append(" isErasedFunctionType")
  if isDependentFunctionType then str.append(" isDependentFunctionType")
  if isTupleN then str.append(" isTupleN")
  if typeArgs.nonEmpty then str.append(" typeArgs: ").append(typeArgs.map(_.show))

  str.toString