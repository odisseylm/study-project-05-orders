package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// ParamClause <: AnyRef
// TermParamClause <: ParamClause
def dumpTermParamClause(using quotes: Quotes)(el: quotes.reflect.TermParamClause, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeParamClause <: ParamClause
def dumpTypeParamClause(using quotes: Quotes)(el: quotes.reflect.TypeParamClause, str: StringBuilder, nextPadLength: Int): Unit = {}
// base ParamClause = ParamClause <: AnyRef
def dumpParamClause(using quotes: Quotes)(el: quotes.reflect.ParamClause, str: StringBuilder, nextPadLength: Int): Unit = {}


