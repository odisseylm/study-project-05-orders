package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// TypedOrTest <: Tree
def dumpTypedOrTest(using quotes: Quotes)(el: quotes.reflect.TypedOrTest, str: StringBuilder, nextPadLength: Int): Unit = {}
// Inferred <: TypeTree
def dumpInferred(using quotes: Quotes)(el: quotes.reflect.Inferred, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeIdent <: TypeTree
def dumpTypeIdent(using quotes: Quotes)(el: quotes.reflect.TypeIdent, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeSelect <: TypeTree
def dumpTypeSelect(using quotes: Quotes)(el: quotes.reflect.TypeSelect, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeProjection <: TypeTree
def dumpTypeProjection(using quotes: Quotes)(el: quotes.reflect.TypeProjection, str: StringBuilder, nextPadLength: Int): Unit = {}
// Singleton <: TypeTree
def dumpSingleton(using quotes: Quotes)(el: quotes.reflect.Singleton, str: StringBuilder, nextPadLength: Int): Unit = {}
// Refined <: TypeTree
def dumpRefined(using quotes: Quotes)(el: quotes.reflect.Refined, str: StringBuilder, nextPadLength: Int): Unit = {}
// Applied <: TypeTree
def dumpApplied(using quotes: Quotes)(el: quotes.reflect.Applied, str: StringBuilder, nextPadLength: Int): Unit = {}
// Annotated <: TypeTree
def dumpAnnotated(using quotes: Quotes)(el: quotes.reflect.Annotated, str: StringBuilder, nextPadLength: Int): Unit = {}
// MatchTypeTree <: TypeTree
def dumpMatchTypeTree(using quotes: Quotes)(el: quotes.reflect.MatchTypeTree, str: StringBuilder, nextPadLength: Int): Unit = {}
// ByName <: TypeTree
def dumpByName(using quotes: Quotes)(el: quotes.reflect.ByName, str: StringBuilder, nextPadLength: Int): Unit = {}
// LambdaTypeTree <: TypeTree
def dumpLambdaTypeTree(using quotes: Quotes)(el: quotes.reflect.LambdaTypeTree, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeBind <: TypeTree
def dumpTypeBind(using quotes: Quotes)(el: quotes.reflect.TypeBind, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeBlock <: TypeTree
def dumpTypeBlock(using quotes: Quotes)(el: quotes.reflect.TypeBlock, str: StringBuilder, nextPadLength: Int): Unit = {}
//
// base TypeTree == TypeTree <: Tree
def dumpBaseTypeTree(using quotes: Quotes)(el: quotes.reflect.TypeTree, str: StringBuilder, nextPadLength: Int): Unit = {}
