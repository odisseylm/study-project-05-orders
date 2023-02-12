package org.mvv.mapstruct.scala.debug

import scala.quoted.*


// Definition <: Statement <: Tree
// Template is not present now in API??..
def dumpTemplate(using quotes: Quotes)(el: quotes.reflect.Tree, str: StringBuilder, nextPadLength: Int): Unit =
  ???


// ClassDef <: Definition
def dumpClassDef(using quotes: Quotes)(classDef: quotes.reflect.ClassDef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = classDef.name
  val constructor: DefDef = classDef.constructor
  val parents: List[Tree] = classDef.parents
  val self: Option[ValDef] = classDef.self
  val body: List[Statement] = classDef.body

  str.addTagName("<ClassDef>", padLength)
    str.addChildTagName("name", name, padLength)

    str.addChildTagName("<constructor>", padLength)
    dumpDefDef(constructor, str, padLength + 2 * padLength)
    str.addChildTagName("</constructor>", padLength)

    str.addChildTagName("<parents>", padLength)
    parents.foreach(s => dumpTree(s, str, padLength + 2 * padLength))
    str.addChildTagName("</parents>", padLength)

    str.addChildTagName("<self>", padLength)
    self.foreach(s => dumpValDef(s, str, padLength + 2 * padLength))
    str.addChildTagName("</self>", padLength)

    str.addChildTagName("<body>", padLength)
    body.foreach(s => dumpTree(s, str, padLength + 2 * padLength))
    str.addChildTagName("</body>", padLength)
  str.addTagName("</ClassDef>", padLength)


// DefDef <: Definition
def dumpDefDef(using quotes: Quotes)(defDef: quotes.reflect.DefDef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = defDef.name
  val paramss: List[ParamClause] = defDef.paramss
  val leadingTypeParams: List[TypeDef] = defDef.leadingTypeParams
  val trailingParamss: List[ParamClause] = defDef.trailingParamss
  val termParamss: List[TermParamClause] = defDef.termParamss
  val returnTpt: TypeTree = defDef.returnTpt
  val rhs: Option[Term] = defDef.rhs

  str.addTagName("<DefDef>", padLength)
    str.addChildTagName("name", name, padLength)

    str.addChildTagName("<paramss>", padLength)
    paramss.foreach(p => dumpParam(p, str, padLength + 2 * padLength))
    str.addChildTagName("</paramss>", padLength)

    str.addChildTagName("<leadingTypeParams>", padLength)
    leadingTypeParams.foreach(td => dumpTypeDef(td, str, padLength + 2 * padLength))
    str.addChildTagName("</leadingTypeParams>", padLength)

    str.addChildTagName("<trailingParamss>", padLength)
    trailingParamss.foreach(p => dumpParam(p, str, padLength + 2 * padLength))
    str.addChildTagName("</trailingParamss>", padLength)

    str.addChildTagName("<termParamss>", padLength)
    termParamss.foreach(p => dumpParam(p, str, padLength + 2 * padLength))
    str.addChildTagName("</termParamss>", padLength)

    str.addChildTagName("<returnTpt>", padLength)
    dumpTree(returnTpt, str, padLength + 2 * padLength)
    str.addChildTagName("</returnTpt>", padLength)

    str.addChildTagName("<rhs>", padLength)
    rhs.foreach(p => dumpTree(p, str, padLength + 2 * padLength))
    str.addChildTagName("</rhs>", padLength)
  str.addTagName("</DefDef>", padLength)


// ValDef <: Definition
def dumpValDef(using quotes: Quotes)(valDef: quotes.reflect.ValDef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = valDef.name
  val tpt: TypeTree = valDef.tpt
  val rhs: Option[Term] = valDef.rhs

  str.addTagName("<ValDef>", padLength)
    str.addChildTagName("name", name, padLength)

    str.addChildTagName("<tpt>", padLength)
    dumpTree(tpt, str, padLength + 2 * padLength)
    str.addChildTagName("</tpt>", padLength)

    str.addChildTagName("<rhs>", padLength)
    rhs.foreach(t => dumpTree(t, str, padLength + 2 * padLength))
    str.addChildTagName("</rhs>", padLength)
  str.addTagName("</ValDef>", padLength)


// TypeDef <: Definition
def dumpTypeDef(using quotes: Quotes)(typeDef: quotes.reflect.TypeDef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = typeDef.name
  val rhs: Tree = typeDef.rhs

  str.addTagName("<TypeDef>", padLength)
    str.addChildTagName("name", name, padLength)
    str.addChildTagName("<rhs>", padLength)
    dumpTree(rhs, str, padLength + 2 * padLength)
    str.addChildTagName("</rhs>", padLength)
  str.addTagName("</TypeDef>", padLength)


// base Definition = Definition <: Statement <: Tree
def dumpDefinition(using quotes: Quotes)(_def: quotes.reflect.Definition, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = _def.name

  str.addTagName("<Definition>", padLength)
    str.addChildTagName("name", name, padLength)
  str.addTagName("</Definition>", padLength)


