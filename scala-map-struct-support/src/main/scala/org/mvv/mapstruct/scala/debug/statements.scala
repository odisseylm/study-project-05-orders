package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// Term <: Statement
//
// Ref <: Term
//
// Wildcard <: Ident <: Ref
def dumpWildcardClause(using quotes: Quotes)(el: quotes.reflect.Wildcard, str: StringBuilder, nextPadLength: Int): Unit = {}
// Ident <: Ref
def dumpIdent(using quotes: Quotes)(el: quotes.reflect.Term, str: StringBuilder, nextPadLength: Int): Unit = {}
// Select <: Ref
def dumpSelect(using quotes: Quotes)(el: quotes.reflect.Select, str: StringBuilder, nextPadLength: Int): Unit = {}
// base Ref = Ref <: Term
def dumpRef(using quotes: Quotes)(el: quotes.reflect.Ref, str: StringBuilder, nextPadLength: Int): Unit = {}


// If <: Term
def dumpIf(using quotes: Quotes)(el: quotes.reflect.If, str: StringBuilder, nextPadLength: Int): Unit = {}
// Match <: Term
def dumpMatch(using quotes: Quotes)(el: quotes.reflect.Match, str: StringBuilder, nextPadLength: Int): Unit = {}
// SummonFrom <: Term
def dumpSummonFrom(using quotes: Quotes)(el: quotes.reflect.SummonFrom, str: StringBuilder, nextPadLength: Int): Unit = {}
// Try <: Term
def dumpTry(using quotes: Quotes)(el: quotes.reflect.Try, str: StringBuilder, nextPadLength: Int): Unit = {}
// Return <: Term
def dumpReturn(using quotes: Quotes)(el: quotes.reflect.Return, str: StringBuilder, nextPadLength: Int): Unit = {}
// Repeated <: Term
def dumpRepeated(using quotes: Quotes)(el: quotes.reflect.Repeated, str: StringBuilder, nextPadLength: Int): Unit = {}
// SelectOuter <: Term
def dumpSelectOuter(using quotes: Quotes)(el: quotes.reflect.SelectOuter, str: StringBuilder, nextPadLength: Int): Unit = {}
// While <: Term
def dumpWhile(using quotes: Quotes)(el: quotes.reflect.While, str: StringBuilder, nextPadLength: Int): Unit = {}


// Apply <: Term
def dumpApply(using quotes: Quotes)(el: quotes.reflect.Apply, str: StringBuilder, nextPadLength: Int): Unit = {}

// Unapply <: Tree
def dumpUnapply(using quotes: Quotes)(el: quotes.reflect.Unapply, str: StringBuilder, nextPadLength: Int): Unit = {}

// Assign <: Term
def dumpAssign(using quotes: Quotes)(el: quotes.reflect.Assign, str: StringBuilder, nextPadLength: Int): Unit = {}


// Bind <: Tree
def dumpBind(using quotes: Quotes)(el: quotes.reflect.Bind, str: StringBuilder, nextPadLength: Int): Unit = {}
// Alternatives <: Tree
def dumpAlternatives(using quotes: Quotes)(el: quotes.reflect.Alternatives, str: StringBuilder, nextPadLength: Int): Unit = {}


// Inlined <: Term
private def dumpInlined(using quotes: Quotes)(inlined: quotes.reflect.Inlined, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val call: Option[Tree] = inlined.call
  val bindings: List[Definition] = inlined.bindings
  val body: Term = inlined.body

  str.addTagName("<Inlined>", padLength)
    str.addChildTagName("<call>", padLength)
    call.foreach(_call => dumpTree(_call, str, padLength + 2 * padLength))
    str.addChildTagName("</call>", padLength)

    str.addChildTagName("<bindings>", padLength)
    bindings.foreach(d => dumpDefinition(d, str, padLength + 2 * padLength))
    str.addChildTagName("</bindings>", padLength)

    str.addChildTagName("<body>", padLength)
    dumpTerm(body, str, padLength + 2 * padLength)
    str.addChildTagName("</body>", padLength)
  str.addTagName("</Inlined>", padLength)
