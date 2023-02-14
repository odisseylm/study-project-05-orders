package org.mvv.mapstruct.scala.debug.dump

import scala.quoted.Quotes


// ImplicitSearchResult <: AnyRef
// ImplicitSearchSuccess <: ImplicitSearchResult
def dumpImplicitSearchSuccess(using quotes: Quotes)(el: quotes.reflect.ImplicitSearchSuccess, str: StringBuilder, nextPadLength: Int): Unit = {}
// ImplicitSearchFailure <: ImplicitSearchResult
// DivergingImplicit <: ImplicitSearchFailure
def dumpDivergingImplicit(using quotes: Quotes)(el: quotes.reflect.DivergingImplicit, str: StringBuilder, nextPadLength: Int): Unit = {}
// NoMatchingImplicits <: ImplicitSearchFailure
def dumpNoMatchingImplicits(using quotes: Quotes)(el: quotes.reflect.NoMatchingImplicits, str: StringBuilder, nextPadLength: Int): Unit = {}
// AmbiguousImplicits <: ImplicitSearchFailure
def dumpAmbiguousImplicits(using quotes: Quotes)(el: quotes.reflect.AmbiguousImplicits, str: StringBuilder, nextPadLength: Int): Unit = {}
// base ImplicitSearchFailure <: ImplicitSearchResult
def dumpImplicitSearchFailure(using quotes: Quotes)(el: quotes.reflect.ImplicitSearchFailure, str: StringBuilder, nextPadLength: Int): Unit = {}
// base ImplicitSearchResult <: AnyRef
def dumpImplicitSearchResult(using quotes: Quotes)(el: quotes.reflect.ImplicitSearchResult, str: StringBuilder, nextPadLength: Int): Unit = {}

