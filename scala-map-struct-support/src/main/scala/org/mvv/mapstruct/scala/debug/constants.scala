package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// Constant <: AnyRef
// BooleanConstant <: Constant
def dumpBooleanConstant(using quotes: Quotes)(el: quotes.reflect.BooleanConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// ByteConstant <: Constant
def dumpByteConstant(using quotes: Quotes)(el: quotes.reflect.ByteConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// ShortConstant <: Constant
def dumpShortConstant(using quotes: Quotes)(el: quotes.reflect.ShortConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// IntConstant <: Constant
def dumpIntConstant(using quotes: Quotes)(el: quotes.reflect.IntConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// LongConstant <: Constant
def dumpLongConstant(using quotes: Quotes)(el: quotes.reflect.LongConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// FloatConstant <: Constant
def dumpFloatConstant(using quotes: Quotes)(el: quotes.reflect.FloatConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// DoubleConstant <: Constant
def dumpDoubleConstant(using quotes: Quotes)(el: quotes.reflect.DoubleConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// CharConstant <: Constant
def dumpCharConstant(using quotes: Quotes)(el: quotes.reflect.CharConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// StringConstant <: Constant
def dumpStringConstant(using quotes: Quotes)(el: quotes.reflect.StringConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// UnitConstant <: Constant
def dumpUnitConstant(using quotes: Quotes)(el: quotes.reflect.UnitConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// NullConstant <: Constant
def dumpNullConstant(using quotes: Quotes)(el: quotes.reflect.NullConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// ClassOfConstant <: Constant
def dumpClassOfConstant(using quotes: Quotes)(el: quotes.reflect.ClassOfConstant, str: StringBuilder, nextPadLength: Int): Unit = {}
// base Constant <: AnyRef
def dumpBaseConstant(using quotes: Quotes)(el: quotes.reflect.Constant, str: StringBuilder, nextPadLength: Int): Unit = {}
