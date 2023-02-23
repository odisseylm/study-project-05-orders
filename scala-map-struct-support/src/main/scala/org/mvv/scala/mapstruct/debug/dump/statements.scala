package org.mvv.scala.mapstruct.debug.dump

import scala.quoted.Quotes
import org.mvv.scala.tools.{ tryDo, getByReflection, unwrapOption, isNull }



def dumpPackageClause(using quotes: Quotes)(packageClause: quotes.reflect.PackageClause, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  str.addTagName("<PackageClause>", padLength)
    str.addChildTagName("<pid>", padLength)
    dumpRef(packageClause.pid, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</pid>", padLength)

    str.addChildTagName("<bindings>", padLength)
    val stats: List[Tree] = packageClause.stats
    stats.foreach(t => dumpTree(t, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</bindings>", padLength)
  str.addTagName("</PackageClause>", padLength)


// Statement <: Tree
//
// Import <: Statement
def dumpImport(using quotes: Quotes)(_import: quotes.reflect.Import, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val expr: Term = _import.expr
  val selectors: List[Selector] = _import.selectors

  str.addTagName("<import>", padLength)
    dumpStatementImpl(_import, str, padLength)

    str.addChildTagName("<expr>", padLength)
      dumpTree(expr, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</expr>", padLength)

    str.addChildTagName("<selectors>", padLength)
      selectors.foreach(s => dumpSelector(s, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</selectors>", padLength)
  str.addTagName("</import>", padLength)


// Export <: Statement
def dumpExport(using quotes: Quotes)(_export: quotes.reflect.Export, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val expr: Term = _export.expr
  val selectors: List[Selector] = _export.selectors

  str.addTagName("<export>", padLength)
    dumpStatementImpl(_export, str, padLength)

    str.addChildTagName("<expr>", padLength)
      dumpTree(expr, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</expr>", padLength)

    str.addChildTagName("<selectors>", padLength)
    selectors.foreach(s => dumpSelector(s, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</selectors>", padLength)
  str.addTagName("</export>", padLength)



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
  dumpExtraData(t, str, padLength)
  str.addTagName("</Ident>", padLength)


// Select <: Ref
def dumpSelect(using quotes: Quotes)(select: quotes.reflect.Select, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val qualifier: Tree = unwrapOption(getByReflection(select, "qualifier")).asInstanceOf[Tree]
  val name: String = select.name
  val signature: Option[Signature] = select.signature

  str.addTagName("<Select>", padLength)
    dumpRefImpl(select, str, padLength)
    str.addChildTagName("name", name, padLength)

    str.addChildTagName("<qualifier>", padLength)
    dumpTree(qualifier, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</qualifier>", padLength)

    signature.foreach { s =>
      str.addChildTagName("<signature>", padLength)
      dumpSignature(s, str, padLength + 2 * indentPerLevel)
      str.addChildTagName("</signature>", padLength)
    }
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
  val cond:  Term = _if.cond
  val thenp: Term = _if.thenp
  val elsep: Term = _if.elsep

  str.addTagName("<If>", padLength)
    dumpTermImpl(_if, str, padLength)

    if isInline then str.addChildTagName("<isInline/>", padLength)

    str.addChildTagName("<cond>", padLength)
    dumpTree(cond, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</cond>", padLength)

    str.addChildTagName("<thenp>", padLength)
    dumpTree(thenp, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</thenp>", padLength)

    str.addChildTagName("<elsep>", padLength)
    dumpTree(elsep, str, padLength + 2 * indentPerLevel)
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

    if isInline then str.addChildTagName("<isInline/>", padLength)

    str.addChildTagName("<scrutinee>", padLength)
    dumpTree(scrutinee, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</scrutinee>", padLength)

    str.addChildTagName("<cases>", padLength)
    cases.foreach(c => dumpCaseDef(c, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</cases>", padLength)
  str.addTagName("</Match>", padLength)


// SummonFrom <: Term
def dumpSummonFrom(using quotes: Quotes)(summonFrom: quotes.reflect.SummonFrom, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val cases: List[CaseDef] = summonFrom.cases

  str.addTagName("<SummonFrom>", padLength)
    dumpTermImpl(summonFrom, str, padLength)

    str.addChildTagName("<cases>", padLength)
    cases.foreach(c => dumpCaseDef(c, str, padLength + 2 * indentPerLevel))
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
    dumpTree(body, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</body>", padLength)

    str.addChildTagName("<cases>", padLength)
    cases.foreach(c => dumpCaseDef(c, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</cases>", padLength)

    str.addChildTagName("<finalizer>", padLength)
    finalizer.foreach(f => dumpTree(f, str, padLength + 2 * indentPerLevel))
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
    dumpTree(expr, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</expr>", padLength)

    str.addChildTagName("<from>", padLength)
    dumpSymbol(from, str, padLength + 2 * indentPerLevel)
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
    elems.foreach(el => dumpTree(el, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</elems>", padLength)

    str.addChildTagName("<elemtpt>", padLength)
    dumpTree(elemtpt, str, padLength + 2 * indentPerLevel)
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
    dumpTree(qualifier, str, padLength + 2 * indentPerLevel)
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
    dumpTree(cond, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</cond>", padLength)

    str.addChildTagName("<body>", padLength)
    dumpTree(body, str, padLength + 2 * indentPerLevel)
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
    args.foreach(arg => dumpTree(arg, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</args>", padLength)

    str.addChildTagName("<fun>", padLength)
    dumpTree(fun, str, padLength + 2 * indentPerLevel)
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
    dumpTree(fun, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</fun>", padLength)

    str.addChildTagName("<implicits>", padLength)
    implicits.foreach(arg => dumpTree(arg, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</implicits>", padLength)

    str.addChildTagName("<patterns>", padLength)
    patterns.foreach(arg => dumpTree(arg, str, padLength + 2 * indentPerLevel))
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
    dumpTree(lhs, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</lhs>", padLength)

    str.addChildTagName("<rhs>", padLength)
    dumpTree(rhs, str, padLength + 2 * indentPerLevel)
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
    dumpTree(pattern, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</pattern>", padLength)
  str.addTagName("</Bind>", padLength)



// Alternatives <: Tree
def dumpAlternatives(using quotes: Quotes)(alternatives: quotes.reflect.Alternatives, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val patterns: List[Tree] = alternatives.patterns

  str.addTagName("<Alternatives>", padLength)
    dumpTreeImpl(alternatives, str, padLength)

    str.addChildTagName("<patterns>", padLength)
    patterns.foreach(p => dumpTree(p, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</patterns>", padLength)
  str.addTagName("</Alternatives>", padLength)



// Inlined <: Term
def dumpInlined(using quotes: Quotes)(inlined: quotes.reflect.Inlined, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val call: Option[Tree] = inlined.call
  val bindings: List[Definition] = inlined.bindings
  val body: Term = inlined.body

  str.addTagName("<Inlined>", padLength)
    dumpTermImpl(inlined, str, padLength)

    str.addChildTagName("<call>", padLength)
    call.foreach(_call => dumpTree(_call, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</call>", padLength)

    str.addChildTagName("<bindings>", padLength)
    bindings.foreach(d => dumpDefinition(d, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</bindings>", padLength)

    str.addChildTagName("<body>", padLength)
    dumpTree(body, str, padLength + 2 * indentPerLevel)
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


// Ref <: Term
private def dumpRefImpl(using quotes: Quotes)(ref: quotes.reflect.Ref, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  dumpTermImpl(ref, str, padLength)


// According to API Ident function creates Term type
private def dumpIdentImpl(using quotes: Quotes)(term: quotes.reflect.Term, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*

  val name: String = treeName(term)
  str.addChildTagName("name", name, padLength)
  dumpTermImpl(term, str, padLength)
  //dumpRefImpl(term, str, padLength)

  /*
  term.toSymbol.foreach { s =>
    val termRef: TermRef = s.termRef
    println(s"termRef: $termRef")
  }
  */



// Block <: Term
def dumpBlock(using quotes: Quotes)(block: quotes.reflect.Block, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<Block>", padLength)
    dumpBlockImpl(block, str, padLength)
  str.addTagName("</Block>", padLength)

// Block <: Term
private def dumpBlockImpl(using quotes: Quotes)(block: quotes.reflect.Block, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val statements: List[Statement] = block.statements
  val expr: Term = block.expr

  dumpTermImpl(block, str, padLength)

  str.addChildTagName("<statements>", padLength)
    statements.foreach(s => dumpTree(s, str, padLength + 2 * indentPerLevel))
  str.addChildTagName("</statements>", padLength)

  dumpTree(expr, str, padLength + 2 * indentPerLevel)


// Closure <: Term
def dumpClosure(using quotes: Quotes)(closure: quotes.reflect.Closure, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val meth: Term = closure.meth
  val tpeOpt: Option[TypeRepr] = closure.tpeOpt

  str.addTagName("<Closure>", padLength)
    dumpTermImpl(closure, str, padLength)

    str.addChildTagName("<meth>", padLength)
      dumpTree(meth, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</meth>", padLength)

    str.addChildTagName("<tpeOpt>", padLength)
      tpeOpt.foreach(tpe => dumpTypeRepr(tpe, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</tpeOpt>", padLength)
  str.addTagName("</Closure>", padLength)


// Lambda: LambdaModule
// ?

//
def dumpLambda(using quotes: Quotes)(tree: quotes.reflect.Block, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val opt: Option[(List[ValDef], Term)] = Lambda.unapply(tree)

  str.addTagName("<Lambda>", padLength)
    dumpBlockImpl(tree, str, padLength)

    opt.foreach { oo =>
      val valDefs: List[ValDef] = oo._1
      val term: Term = oo._2

      str.addChildTagName("<mvalDefs>", padLength)
        valDefs.foreach(vd => dumpTree(vd, str, padLength + 2 * indentPerLevel))
      str.addChildTagName("</valDefs>", padLength)

      str.addChildTagName("<term>", padLength)
        dumpTree(term, str, padLength + 2 * indentPerLevel)
      str.addChildTagName("</term>", padLength)
    }
  str.addTagName("</Lambda>", padLength)



def treeName(using quotes: Quotes)(tree: quotes.reflect.Tree): String =
  import quotes.reflect.*
  // T O D O: use also
  // Definition.name:
  // Select.name:
  // NamedArg.name:
  // SelectOuter.name:
  // TypeIdent.name:
  // TypeSelect.name:
  // TypeProjection.name:
  // TypeBind.name:
  // SimpleSelector.name:
  // OmitSelector.name:
  // NamedType.name:
  // Refinement.name:
  // Refinement.name:
  // Bind.name:
  val name: String =
    tryDo { tree.asInstanceOf[Ident].name }
    .orElse( tryDo { getByReflection(tree, "name", "getName").asInstanceOf[String] } )
    .orElse( tree.toSymbol .map(_.name)) .getOrElse("?name?")
  name


// Term <: Statement
private def dumpTermImpl(using quotes: Quotes)(term: quotes.reflect.Term, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpe: TypeRepr = term.tpe

  // I guess we should not dump it
  //val underlyingArgument: Term = term.underlyingArgument
  //val underlying: Term = term.underlying
  //val appliedToNone: Term = term.appliedToNone

  dumpStatementImpl(term, str, padLength)
  str.addChildTagName("tpe", typeReprToString(tpe), padLength)


// Statement <: Tree
private def dumpStatementImpl(using quotes: Quotes)(statement: quotes.reflect.Statement, str: StringBuilder, padLength: Int): Unit =
  dumpTreeImpl(statement, str, padLength)


// Tree <: AnyRef
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

  if isExpr then str.addChildTagName("<isExpr/>", padLength)
  pos.foreach(p => str.addChildTagName("position", positionToString(p), padLength))
  symbol.foreach(s => str.addChildTagName("symbol", symbolToString(s), padLength))
  show.foreach(s => str.addChildTagName("show", s, padLength))
  asExpr.foreach(e => str.addChildTagName("asExpr", e, padLength))

  str.addChildTagName("javaClassName", tree.getClass.nn.getSimpleName.nn, padLength)
  str.addChildTagName("javaClassFullName", tree.getClass.nn.getName.nn, padLength)

def dumpExtraData(obj: Any, str: StringBuilder, padLength: Int): Unit =
  val extraInfo: Option[Any] = getExtraInfo(obj)
  extraInfo.foreach(e => str.addChildTagName("extra", e, padLength))


// !!! WARN: use it ONLY as super.dump() !!!
//
// base Term
def dumpBaseTerm(using quotes: Quotes)(term: quotes.reflect.Term, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<Term>", padLength)
    dumpTermImpl(term, str, padLength)
  str.addTagName("<Term>", padLength)


// !!! WARN: use it ONLY as super.dump() !!!
//
// base Statement
def dumpBaseStatement(using quotes: Quotes)(statement: quotes.reflect.Statement, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<Statement>", padLength)
    dumpStatementImpl(statement, str, padLength)
  str.addTagName("<Statement>", padLength)


def getExtraInfo(obj: Any): Option[Any] =
  val productElsData = try {
    val products = unwrapOption(getByReflection(obj, "productIterator")).asInstanceOf[Iterator[Any]].toList
    products.zipWithIndex.map((v, i) => s"$i: ${v.getClass.nn.getSimpleName} = $v")
  } catch case _: Exception => ""
  if isObjEmpty(productElsData) then None else Option(productElsData)


//noinspection TypeCheckCanBeMatch
private def isObjEmpty(obj: Any): Boolean =
  if obj.isNull then return true
  if obj.isInstanceOf[List[?]] && obj.asInstanceOf[List[?]].isEmpty then return true
  if obj.isInstanceOf[String]  && obj.asInstanceOf[String].isBlank  then return true
  false

