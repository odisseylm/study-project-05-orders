package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes
import org.mvv.mapstruct.scala.{ tryDo, getByReflection }


// Term <: Statement
//
// Ref <: Term
//
// Wildcard <: Ident <: Ref
def dumpWildcardClause(using quotes: Quotes)(w: quotes.reflect.Wildcard, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  str.addTagName("<Wildcard>", padLength)
  dumpIdentImpl(w, str, padLength)
  str.addTagName("</Wildcard>", padLength)


// Ident <: Ref
def dumpIdent(using quotes: Quotes)(t: quotes.reflect.Term, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<Ident>", padLength)
  dumpIdentImpl(t, str, padLength)
  str.addTagName("</Ident>", padLength)


// Select <: Ref
def dumpSelect(using quotes: Quotes)(select: quotes.reflect.Select, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val qualifier: Term = select.qualifier
  val name: String = select.name
  val signature: Option[Signature] = select.signature

  str.addTagName("<Select>", padLength)
    dumpRefImpl(select, str, padLength)
    str.addChildTagName("name", name, padLength)

    str.addChildTagName("<qualifier>", padLength)
    dumpTree(qualifier, str, padLength + 2 * padLength)
    str.addChildTagName("</qualifier>", padLength)

    str.addChildTagName("<signature>", padLength)
    signature.foreach(s => dumpSignature(s, str, padLength + 2 * padLength))
    str.addChildTagName("</signature>", padLength)
  str.addTagName("</Select>", padLength)


// base Ref = Ref <: Term
def dumpRef(using quotes: Quotes)(ref: quotes.reflect.Ref, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  str.addTagName("<Ref>", padLength)
  dumpRefImpl(ref, str, padLength)
  str.addTagName("</Ref>", padLength)




// If <: Term
def dumpIf(using quotes: Quotes)(_if: quotes.reflect.If, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val isInline: Boolean = _if.isInline
  val cond: Term = _if.cond
  val thenp: Term = _if.thenp
  val elsep: Term = _if.elsep

  str.addTagName("<If>", padLength)
    dumpTermImpl(_if, str, padLength)

    str.addChildTagName("isInline", isInline, padLength)

    str.addChildTagName("<cond>", padLength)
    dumpTree(cond, str, padLength + 2 * padLength)
    str.addChildTagName("</cond>", padLength)

    str.addChildTagName("<thenp>", padLength)
    dumpTree(thenp, str, padLength + 2 * padLength)
    str.addChildTagName("</thenp>", padLength)

    str.addChildTagName("<elsep>", padLength)
    dumpTree(elsep, str, padLength + 2 * padLength)
    str.addChildTagName("</elsep>", padLength)
  str.addTagName("</If>", padLength)


// Match <: Term
def dumpMatch(using quotes: Quotes)(_match: quotes.reflect.Match, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val isInline: Boolean = _match.isInline
  val scrutinee: Term = _match.scrutinee
  val cases: List[CaseDef] = _match.cases

  str.addTagName("<Match>", padLength)
    dumpTermImpl(_match, str, padLength)

    str.addChildTagName("isInline", isInline, padLength)

    str.addChildTagName("<scrutinee>", padLength)
    dumpTree(scrutinee, str, padLength + 2 * padLength)
    str.addChildTagName("</scrutinee>", padLength)

    str.addChildTagName("<cases>", padLength)
    cases.foreach(c => dumpCaseDef(c, str, padLength + 2 * padLength))
    str.addChildTagName("</cases>", padLength)
  str.addTagName("</Match>", padLength)


// SummonFrom <: Term
def dumpSummonFrom(using quotes: Quotes)(summonFrom: quotes.reflect.SummonFrom, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val cases: List[CaseDef] = summonFrom.cases

  str.addTagName("<SummonFrom>", padLength)
    dumpTermImpl(summonFrom, str, padLength)

    str.addChildTagName("<cases>", padLength)
    cases.foreach(c => dumpCaseDef(c, str, padLength + 2 * padLength))
    str.addChildTagName("</cases>", padLength)
  str.addTagName("</SummonFrom>", padLength)


// Try <: Term
def dumpTry(using quotes: Quotes)(_try: quotes.reflect.Try, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val body: Term = _try.body
  val cases: List[CaseDef] = _try.cases
  val finalizer: Option[Term] = _try.finalizer

  str.addTagName("<Try>", padLength)
    dumpTermImpl(_try, str, padLength)

    str.addChildTagName("<body>", padLength)
    dumpTree(body, str, padLength + 2 * padLength)
    str.addChildTagName("</body>", padLength)

    str.addChildTagName("<cases>", padLength)
    cases.foreach(c => dumpCaseDef(c, str, padLength + 2 * padLength))
    str.addChildTagName("</cases>", padLength)

    str.addChildTagName("<finalizer>", padLength)
    finalizer.foreach(f => dumpTree(f, str, padLength + 2 * padLength))
    str.addChildTagName("</finalizer>", padLength)
  str.addTagName("</Try>", padLength)


// Return <: Term
def dumpReturn(using quotes: Quotes)(_return: quotes.reflect.Return, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val expr: Term = _return.expr
  val from: Symbol = _return.from

  str.addTagName("<Return>", padLength)
    dumpTermImpl(_return, str, padLength)

    str.addChildTagName("<expr>", padLength)
    dumpTree(expr, str, padLength + 2 * padLength)
    str.addChildTagName("</expr>", padLength)

    str.addChildTagName("<from>", padLength)
    dumpSymbol(from, str, padLength + 2 * padLength)
    str.addChildTagName("</from>", padLength)
  str.addTagName("</Return>", padLength)



// Repeated <: Term
def dumpRepeated(using quotes: Quotes)(repeated: quotes.reflect.Repeated, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val elems: List[Term] = repeated.elems
  val elemtpt: TypeTree = repeated.elemtpt

  str.addTagName("<Repeated>", padLength)
    dumpTermImpl(repeated, str, padLength)

    str.addChildTagName("<elems>", padLength)
    elems.foreach(el => dumpTree(el, str, padLength + 2 * padLength))
    str.addChildTagName("</elems>", padLength)

    str.addChildTagName("<elemtpt>", padLength)
    dumpTree(elemtpt, str, padLength + 2 * padLength)
    str.addChildTagName("</elemtpt>", padLength)
  str.addTagName("</Repeated>", padLength)



// SelectOuter <: Term
def dumpSelectOuter(using quotes: Quotes)(selectOuter: quotes.reflect.SelectOuter, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = selectOuter.name
  val level: Int = selectOuter.level
  val qualifier: Term = selectOuter.qualifier

  str.addTagName("<SelectOuter>", padLength)
    dumpTermImpl(selectOuter, str, padLength)

    str.addChildTagName("name", name, padLength)
    str.addChildTagName("level", level, padLength)

    str.addChildTagName("<qualifier>", padLength)
    dumpTree(qualifier, str, padLength + 2 * padLength)
    str.addChildTagName("</qualifier>", padLength)
  str.addTagName("</SelectOuter>", padLength)



// While <: Term
def dumpWhile(using quotes: Quotes)(_while: quotes.reflect.While, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val cond: Term = _while.cond
  val body: Term = _while.body

  str.addTagName("<While>", padLength)
    dumpTermImpl(_while, str, padLength)

    str.addChildTagName("<cond>", padLength)
    dumpTree(cond, str, padLength + 2 * padLength)
    str.addChildTagName("</cond>", padLength)

    str.addChildTagName("<body>", padLength)
    dumpTree(body, str, padLength + 2 * padLength)
    str.addChildTagName("</body>", padLength)
  str.addTagName("</While>", padLength)




// Apply <: Term
def dumpApply(using quotes: Quotes)(apply: quotes.reflect.Apply, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val args: List[Term] = apply.args
  val fun: Term = apply.fun

  str.addTagName("<Apply>", padLength)
    dumpTermImpl(apply, str, padLength)

    str.addChildTagName("<args>", padLength)
    args.foreach(arg => dumpTree(arg, str, padLength + 2 * padLength))
    str.addChildTagName("</args>", padLength)

    str.addChildTagName("<fun>", padLength)
    dumpTree(fun, str, padLength + 2 * padLength)
    str.addChildTagName("</fun>", padLength)
  str.addTagName("</Apply>", padLength)



// Unapply <: Tree
def dumpUnapply(using quotes: Quotes)(unapply: quotes.reflect.Unapply, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val fun: Term = unapply.fun
  val implicits: List[Term] = unapply.implicits
  val patterns: List[Tree] = unapply.patterns

  str.addTagName("<Unapply>", padLength)
    dumpTreeImpl(unapply, str, padLength)

    str.addChildTagName("<fun>", padLength)
    dumpTree(fun, str, padLength + 2 * padLength)
    str.addChildTagName("</fun>", padLength)

    str.addChildTagName("<implicits>", padLength)
    implicits.foreach(arg => dumpTree(arg, str, padLength + 2 * padLength))
    str.addChildTagName("</implicits>", padLength)

    str.addChildTagName("<patterns>", padLength)
    patterns.foreach(arg => dumpTree(arg, str, padLength + 2 * padLength))
    str.addChildTagName("</patterns>", padLength)
  str.addTagName("</Unapply>", padLength)



// Assign <: Term
def dumpAssign(using quotes: Quotes)(assign: quotes.reflect.Assign, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val lhs: Tree = assign.lhs
  val rhs: Tree = assign.rhs

  str.addTagName("<Assign>", padLength)
    dumpTermImpl(assign, str, padLength)

    str.addChildTagName("<lhs>", padLength)
    dumpTree(lhs, str, padLength + 2 * padLength)
    str.addChildTagName("</lhs>", padLength)

    str.addChildTagName("<rhs>", padLength)
    dumpTree(rhs, str, padLength + 2 * padLength)
    str.addChildTagName("</rhs>", padLength)
  str.addTagName("</Assign>", padLength)




// Bind <: Tree
def dumpBind(using quotes: Quotes)(bind: quotes.reflect.Bind, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = bind.name
  val pattern: Tree = bind.pattern

  str.addTagName("<Bind>", padLength)
    str.addChildTagName("name", name, padLength)

    dumpTreeImpl(bind, str, padLength)

    str.addChildTagName("<pattern>", padLength)
    dumpTree(pattern, str, padLength + 2 * padLength)
    str.addChildTagName("</pattern>", padLength)
  str.addTagName("</Bind>", padLength)



// Alternatives <: Tree
def dumpAlternatives(using quotes: Quotes)(alternatives: quotes.reflect.Alternatives, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val patterns: List[Tree] = alternatives.patterns

  str.addTagName("<Alternatives>", padLength)
    dumpTreeImpl(alternatives, str, padLength)

    str.addChildTagName("<patterns>", padLength)
    patterns.foreach(p => dumpTree(p, str, padLength + 2 * padLength))
    str.addChildTagName("</patterns>", padLength)
  str.addTagName("</Alternatives>", padLength)




// Inlined <: Term
private def dumpInlined(using quotes: Quotes)(inlined: quotes.reflect.Inlined, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val call: Option[Tree] = inlined.call
  val bindings: List[Definition] = inlined.bindings
  val body: Term = inlined.body

  str.addTagName("<Inlined>", padLength)
    dumpTermImpl(inlined, str, padLength)

    str.addChildTagName("<call>", padLength)
    call.foreach(_call => dumpTree(_call, str, padLength + 2 * padLength))
    str.addChildTagName("</call>", padLength)

    str.addChildTagName("<bindings>", padLength)
    bindings.foreach(d => dumpDefinition(d, str, padLength + 2 * padLength))
    str.addChildTagName("</bindings>", padLength)

    str.addChildTagName("<body>", padLength)
    dumpTree(body, str, padLength + 2 * padLength)
    str.addChildTagName("</body>", padLength)
  str.addTagName("</Inlined>", padLength)



// Signature <: AnyRef
private def dumpSignature(using quotes: Quotes)(signature: quotes.reflect.Signature, str: StringBuilder, padLength: Int) =
  import quotes.reflect.*
  val paramSigs: List[String | Int] = signature.paramSigs
  val resultSig: String = signature.resultSig

  str.addTagName("<Signature>", padLength)
    str.addChildTagName("resultSig", resultSig, padLength)

    str.addChildTagName("<paramSigs>", padLength)
    paramSigs.foreach(p => str.addChildTagName("paramSig", p, padLength))
    str.addChildTagName("</paramSigs>", padLength)
  str.addTagName("</Signature>", padLength)


private def dumpRefImpl(using quotes: Quotes)(ref: quotes.reflect.Ref, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  dumpTermImpl(ref, str, padLength)


private def dumpIdentImpl(using quotes: Quotes)(term: quotes.reflect.Term, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = tryDo { term.asInstanceOf[Ident].name }
    .orElse( tryDo { getByReflection(term, "name", "getName").asInstanceOf[String] } )
    .orElse( term.toSymbol .map(_.name)) .getOrElse("?name?")
  str.addChildTagName("name", name, padLength)
  dumpTermImpl(term, str, padLength)


private def dumpTermImpl(using quotes: Quotes)(term: quotes.reflect.Term, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpe: TypeRepr = term.tpe

  // I guess we should not dump it
  //val underlyingArgument: Term = term.underlyingArgument
  //val underlying: Term = term.underlying
  //val appliedToNone: Term = term.appliedToNone

  str.addChildTagName("tpe", typeReprToString(tpe), padLength)


private def dumpTreeImpl(using quotes: Quotes)(tree: quotes.reflect.Tree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*

  val toShowPosition = false
  val toShowExpr = false
  val toShowShow = false
  val toShowSymbol = false

  val isExpr: Boolean = tree.isExpr

  val pos: Option[Position] = if toShowPosition then Option(tree.pos) else None
  val symbol: Option[Symbol] = if toShowSymbol then Option(tree.symbol) else None
  val show: Option[String] = if toShowShow then Option(tree.show) else None
  val asExpr: Option[scala.quoted.Expr[Any]] = if toShowExpr then Option(tree.asExpr) else None
  //val asExprOf[T] scala.quoted.Expr[T]

  pos.foreach(p => str.addChildTagName("position", positionToString(p), padLength))
  symbol.foreach(s => str.addChildTagName("symbol", symbolToString(s), padLength))
  show.foreach(s => str.addChildTagName("show", s, padLength))
  asExpr.foreach(e => str.addChildTagName("asExpr", e, padLength))



