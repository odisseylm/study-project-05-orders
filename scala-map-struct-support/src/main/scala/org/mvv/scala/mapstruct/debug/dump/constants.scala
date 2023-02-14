package org.mvv.mapstruct.scala.debug.dump

import scala.quoted.Quotes


// Constant <: AnyRef
// BooleanConstant <: Constant
def dumpBooleanConstant(using quotes: Quotes)(c: quotes.reflect.BooleanConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Boolean] = BooleanConstant.unapply(c)
  str.addChildTagName("BooleanConstant", v.get, padLength)


// ByteConstant <: Constant
def dumpByteConstant(using quotes: Quotes)(c: quotes.reflect.ByteConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Byte] = ByteConstant.unapply(c)
  str.addChildTagName("ByteConstant", v.get, padLength)


// ShortConstant <: Constant
def dumpShortConstant(using quotes: Quotes)(c: quotes.reflect.ShortConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Short] = ShortConstant.unapply(c)
  str.addChildTagName("ShortConstant", v.get, padLength)


// IntConstant <: Constant
def dumpIntConstant(using quotes: Quotes)(c: quotes.reflect.IntConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Int] = IntConstant.unapply(c)
  str.addChildTagName("IntConstant", v.get, padLength)


// LongConstant <: Constant
def dumpLongConstant(using quotes: Quotes)(c: quotes.reflect.LongConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Long] = LongConstant.unapply(c)
  str.addChildTagName("LongConstant", v.get, padLength)


// FloatConstant <: Constant
def dumpFloatConstant(using quotes: Quotes)(c: quotes.reflect.FloatConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Float] = FloatConstant.unapply(c)
  str.addChildTagName("FloatConstant", v.get, padLength)


// DoubleConstant <: Constant
def dumpDoubleConstant(using quotes: Quotes)(c: quotes.reflect.DoubleConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Double] = DoubleConstant.unapply(c)
  str.addChildTagName("DoubleConstant", v.get, padLength)


// CharConstant <: Constant
def dumpCharConstant(using quotes: Quotes)(c: quotes.reflect.CharConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[Char] = CharConstant.unapply(c)
  str.addChildTagName("CharConstant", v.get, padLength)


// StringConstant <: Constant
def dumpStringConstant(using quotes: Quotes)(c: quotes.reflect.StringConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Some[String] = StringConstant.unapply(c)
  str.addChildTagName("StringConstant", v.get, padLength)


// UnitConstant <: Constant
def dumpUnitConstant(using quotes: Quotes)(c: quotes.reflect.UnitConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  //val v: true = UnitConstant.unapply(c)
  str.addChildTagName("UnitConstant", "scala.Unit", padLength)


// NullConstant <: Constant
def dumpNullConstant(using quotes: Quotes)(c: quotes.reflect.NullConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  //val v: Some[Null] = NullConstant.unapply(c)
  str.addChildTagName("NullConstant", "null", padLength)


// ClassOfConstant <: Constant
def dumpClassOfConstant(using quotes: Quotes)(c: quotes.reflect.ClassOfConstant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val v: Option[TypeRepr] = ClassOfConstant.unapply(c)
  str.addChildTagName("ClassOfConstant", v.map(_.show).getOrElse("???"), padLength)


// base Constant <: AnyRef
def dumpBaseConstant(using quotes: Quotes)(c: quotes.reflect.Constant, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val vAsString: String = c.show
  //val vAsAny: Any = c.value
  str.addChildTagName("Constant", vAsString, padLength)
