package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// type TypeRepr
// ConstantType <: TypeRepr
def dumpConstantType(using quotes: Quotes)(el: quotes.reflect.ConstantType, str: StringBuilder, nextPadLength: Int): Unit = {}
// NamedType <: TypeRepr
// TermRef <: NamedType <: TypeRepr
def dumpTermRef(using quotes: Quotes)(el: quotes.reflect.TermRef, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeRef <: NamedType <: TypeRepr
def dumpTypeRef(using quotes: Quotes)(el: quotes.reflect.TypeRef, str: StringBuilder, nextPadLength: Int): Unit = {}
// base NamedType = NamedType <: TypeRepr
def dumpNamedType(using quotes: Quotes)(el: quotes.reflect.NamedType, str: StringBuilder, nextPadLength: Int): Unit = {}

// SuperType <: TypeRepr
def dumpSuperType(using quotes: Quotes)(el: quotes.reflect.SuperType, str: StringBuilder, nextPadLength: Int): Unit = {}
// Refinement <: TypeRepr
def dumpRefinement(using quotes: Quotes)(el: quotes.reflect.Refinement, str: StringBuilder, nextPadLength: Int): Unit = {}
// AppliedType <: TypeRepr
def dumpAppliedType(using quotes: Quotes)(el: quotes.reflect.AppliedType, str: StringBuilder, nextPadLength: Int): Unit = {}
// AnnotatedType <: TypeRepr
def dumpAnnotatedType(using quotes: Quotes)(el: quotes.reflect.AnnotatedType, str: StringBuilder, nextPadLength: Int): Unit = {}
// AndOrType <: TypeRepr
def dumpAndOrType(using quotes: Quotes)(el: quotes.reflect.AndOrType, str: StringBuilder, nextPadLength: Int): Unit = {}
// AndType <: AndOrType
def dumpAndType(using quotes: Quotes)(el: quotes.reflect.AndType, str: StringBuilder, nextPadLength: Int): Unit = {}
// OrType <: AndOrType
def dumpOrType(using quotes: Quotes)(el: quotes.reflect.OrType, str: StringBuilder, nextPadLength: Int): Unit = {}
// MatchType <: TypeRepr
def dumpMatchType(using quotes: Quotes)(el: quotes.reflect.MatchType, str: StringBuilder, nextPadLength: Int): Unit = {}
// ByNameType <: TypeRepr
def dumpByNameType(using quotes: Quotes)(el: quotes.reflect.ByNameType, str: StringBuilder, nextPadLength: Int): Unit = {}
// ParamRef <: TypeRepr
def dumpParamRef(using quotes: Quotes)(el: quotes.reflect.ParamRef, str: StringBuilder, nextPadLength: Int): Unit = {}
// ThisType <: TypeRepr
def dumpThisType(using quotes: Quotes)(el: quotes.reflect.ThisType, str: StringBuilder, nextPadLength: Int): Unit = {}
// RecursiveThis <: TypeRepr
def dumpRecursiveThis(using quotes: Quotes)(el: quotes.reflect.RecursiveThis, str: StringBuilder, nextPadLength: Int): Unit = {}
// RecursiveType <: TypeRepr
def dumpRecursiveType(using quotes: Quotes)(el: quotes.reflect.RecursiveType, str: StringBuilder, nextPadLength: Int): Unit = {}
//
// LambdaType <: TypeRepr
// MethodOrPoly <: LambdaType
// MethodType <: MethodOrPoly
def dumpMethodType(using quotes: Quotes)(el: quotes.reflect.MethodType, str: StringBuilder, nextPadLength: Int): Unit = {}
// PolyType <: MethodOrPoly
def dumpPolyType(using quotes: Quotes)(el: quotes.reflect.PolyType, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeLambda <: LambdaType
def dumpTypeLambda(using quotes: Quotes)(el: quotes.reflect.TypeLambda, str: StringBuilder, nextPadLength: Int): Unit = {}
// base MethodOrPoly <: LambdaType
def dumpMethodOrPoly(using quotes: Quotes)(el: quotes.reflect.MethodOrPoly, str: StringBuilder, nextPadLength: Int): Unit = {}
// base LambdaType = LambdaType <: TypeRepr
def dumpLambdaType(using quotes: Quotes)(el: quotes.reflect.LambdaType, str: StringBuilder, nextPadLength: Int): Unit = {}

//
// MatchCase <: TypeRepr
def dumpMatchCase(using quotes: Quotes)(el: quotes.reflect.MatchCase, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeBounds <: TypeRepr
def dumpTypeBounds(using quotes: Quotes)(el: quotes.reflect.TypeBounds, str: StringBuilder, nextPadLength: Int): Unit = {}
// NoPrefix <: TypeRepr
def dumpNoPrefix(using quotes: Quotes)(el: quotes.reflect.NoPrefix, str: StringBuilder, nextPadLength: Int): Unit = {}
// base type TypeRepr
def dumpBaseTypeRepr(using quotes: Quotes)(el: quotes.reflect.TypeRepr, str: StringBuilder, nextPadLength: Int): Unit = {}


def typeReprToString(using quotes: Quotes)(typeRepr: quotes.reflect.TypeRepr): String =
  ???