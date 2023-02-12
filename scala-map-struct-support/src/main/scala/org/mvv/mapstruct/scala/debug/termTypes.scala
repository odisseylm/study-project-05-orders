package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// Literal <: Term
def dumpLiteral(using quotes: Quotes)(el: quotes.reflect.Literal, str: StringBuilder, nextPadLength: Int): Unit = {}
// This <: Term
def dumpThis(using quotes: Quotes)(el: quotes.reflect.This, str: StringBuilder, nextPadLength: Int): Unit = {}
// New <: Term
def dumpNew(using quotes: Quotes)(el: quotes.reflect.New, str: StringBuilder, nextPadLength: Int): Unit = {}
// NamedArg <: Term
def dumpNamedArg(using quotes: Quotes)(el: quotes.reflect.NamedArg, str: StringBuilder, nextPadLength: Int): Unit = {}
// TypeApply <: Term
def dumpTypeApply(using quotes: Quotes)(el: quotes.reflect.TypeApply, str: StringBuilder, nextPadLength: Int): Unit = {}
// Super <: Term
def dumpSuper(using quotes: Quotes)(el: quotes.reflect.Super, str: StringBuilder, nextPadLength: Int): Unit = {}
// Typed <: Term & TypedOrTest
def dumpTyped(using quotes: Quotes)(el: quotes.reflect.Typed, str: StringBuilder, nextPadLength: Int): Unit = {}

