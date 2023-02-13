package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// Literal <: Term
def dumpLiteral(using quotes: Quotes)(l: quotes.reflect.Literal, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val constant: Constant = l.constant
  
  str.addTagName("<Literal>", padLength)
    str.addChildTagName("<constant>", padLength)
    dumpConstant(constant, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</constant>", padLength)
  str.addTagName("</Literal>", padLength)

  
// This <: Term
def dumpThis(using quotes: Quotes)(_this: quotes.reflect.This, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val id: Option[String] = _this.id
  
  str.addTagName("<This>", padLength)
    dumpTermImpl(_this, str, padLength + 2 * indentPerLevel)
    id.foreach(idd => str.addChildTagName("id", idd, padLength))
  str.addTagName("</This>", padLength)

  
// New <: Term
def dumpNew(using quotes: Quotes)(_new: quotes.reflect.New, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpt: TypeTree = _new.tpt

  str.addTagName("<New>", padLength)
    dumpTermImpl(_new, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("<tpt>", padLength)
    dumpTree(tpt, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</tpt>", padLength)
  str.addTagName("</New>", padLength)


// NamedArg <: Term
def dumpNamedArg(using quotes: Quotes)(namedArg: quotes.reflect.NamedArg, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  def name: String = namedArg.name
  def value: Term = namedArg.value

  str.addTagName("<NamedArg>", padLength)
    dumpTermImpl(namedArg, str, padLength + 2 * indentPerLevel)

    str.addChildTagName("name", name, padLength)

    str.addChildTagName("<value>", padLength)
    dumpTree(value, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</value>", padLength)
  str.addTagName("</NamedArg>", padLength)



// TypeApply <: Term
def dumpTypeApply(using quotes: Quotes)(typeApply: quotes.reflect.TypeApply, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  def fun: Term = typeApply.fun
  def args: List[TypeTree] = typeApply.args

  str.addTagName("<TypeApply>", padLength)
    dumpTermImpl(typeApply, str, padLength + 2 * indentPerLevel)

    str.addChildTagName("<fun>", padLength)
    dumpTree(fun, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</fun>", padLength)

    str.addChildTagName("<args>", padLength)
    args.foreach(a => dumpTree(a, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</args>", padLength)
  str.addTagName("</TypeApply>", padLength)


// Super <: Term
def dumpSuper(using quotes: Quotes)(_super: quotes.reflect.Super, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  def qualifier: Term = _super.qualifier
  def id: Option[String] = _super.id
  def idPos: Position = _super.idPos

  str.addTagName("<Super>", padLength)
    dumpTermImpl(_super, str, padLength + 2 * indentPerLevel)

    str.addChildTagName("<qualifier>", padLength)
    dumpTree(qualifier, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</qualifier>", padLength)

    id.foreach( idd => str.addChildTagName("id", idd, padLength) )
    str.addChildTagName("idPos", positionToString(idPos), padLength)
  str.addTagName("</Super>", padLength)



// Typed <: Term & TypedOrTest
def dumpTyped(using quotes: Quotes)(typed: quotes.reflect.Typed, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  def expr: Term = typed.expr
  def tpt: TypeTree = typed.tpt: TypeTree

  str.addTagName("<Typed>", padLength)
    dumpTermImpl(typed, str, padLength + 2 * indentPerLevel)

    str.addChildTagName("<expr>", padLength)
    dumpTree(expr, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</expr>", padLength)

    str.addChildTagName("<tpt>", padLength)
    dumpTree(tpt, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</tpt>", padLength)
  str.addTagName("</Typed>", padLength)

