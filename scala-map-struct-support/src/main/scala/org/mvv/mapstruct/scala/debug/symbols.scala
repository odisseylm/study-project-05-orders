package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// Symbol <: AnyRef
def dumpSymbol(using quotes: Quotes)(el: quotes.reflect.Symbol, str: StringBuilder, nextPadLength: Int): Unit = {}

// type Flags
def dumpFlags(using quotes: Quotes)(el: quotes.reflect.Flags, str: StringBuilder, nextPadLength: Int): Unit = {}

// type Position <: AnyRef
def dumpPosition(using quotes: Quotes)(el: quotes.reflect.Position, str: StringBuilder, nextPadLength: Int): Unit = {}
// SourceFile <: AnyRef
def dumpSourceFile(using quotes: Quotes)(el: quotes.reflect.SourceFile, str: StringBuilder, nextPadLength: Int): Unit = {}

