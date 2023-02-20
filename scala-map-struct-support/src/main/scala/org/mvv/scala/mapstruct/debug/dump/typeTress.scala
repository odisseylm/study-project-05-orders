package org.mvv.scala.mapstruct.debug.dump

import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ getByReflection, unwrapOption }


// TypedOrTest <: Tree
def dumpTypedOrTest(using quotes: Quotes)(typedOrTest: quotes.reflect.TypedOrTest, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tree: Tree = typedOrTest.tree
  val tpt: TypeTree = typedOrTest.tpt
  
  str.addTagName("<TypedOrTest>", padLength)
    dumpTreeImpl(typedOrTest, str, padLength)

    str.addChildTagName("<tree>", padLength)
      dumpTreeImpl(tree, str, padLength + 2 *indentPerLevel)
    str.addChildTagName("</tree>", padLength)

    str.addChildTagName("<tpt>", padLength)
      dumpTreeImpl(tpt, str, padLength + 2 *indentPerLevel)
    str.addChildTagName("</tpt>", padLength)
  str.addTagName("</TypedOrTest>", padLength)


// Inferred <: TypeTree
def dumpInferred(using quotes: Quotes)(inferred: quotes.reflect.Inferred, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<Inferred>", padLength)
    dumpTypeTreeImpl(inferred, str, padLength)
    // no other fields
  str.addTagName("</Inferred>", padLength)


// TypeIdent <: TypeTree
def dumpTypeIdent(using quotes: Quotes)(typeIdent: quotes.reflect.TypeIdent, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = typeIdent.name

  str.addTagName("<TypeIdent>", padLength)
    str.addChildTagName("name", name, padLength)
    dumpTypeTreeImpl(typeIdent, str, padLength)
  str.addTagName("</TypeIdent>", padLength)


// TypeSelect <: TypeTree
def dumpTypeSelect(using quotes: Quotes)(typeSelect: quotes.reflect.TypeSelect, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val qualifier: Term = typeSelect.qualifier
  val name: String = typeSelect.name

  str.addTagName("<TypeSelect>", padLength)
    dumpTypeTreeImpl(typeSelect, str, padLength)

    str.addChildTagName("<qualifier>", padLength)
      dumpTree(qualifier, str, padLength)
    str.addChildTagName("</qualifier>", padLength)

    str.addChildTagName("name", name, padLength)
  str.addTagName("</TypeSelect>", padLength)



// TypeProjection <: TypeTree
def dumpTypeProjection(using quotes: Quotes)(typeProjection: quotes.reflect.TypeProjection, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val qualifier: TypeTree = typeProjection.qualifier
  val name: String = typeProjection.name

  str.addTagName("<TypeProjection>", padLength)
    dumpTypeTreeImpl(typeProjection, str, padLength)

    str.addChildTagName("<qualifier>", padLength)
      dumpTree(qualifier, str, padLength)
    str.addChildTagName("</qualifier>", padLength)

    str.addChildTagName("name", name, padLength)
  str.addTagName("</TypeProjection>", padLength)



// Singleton <: TypeTree
def dumpSingleton(using quotes: Quotes)(singleton: quotes.reflect.Singleton, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val ref: Term = singleton.ref

  str.addTagName("<Singleton>", padLength)
    dumpTypeTreeImpl(singleton, str, padLength)

    str.addChildTagName("<ref>", padLength)
      dumpTree(ref, str, padLength)
    str.addChildTagName("</ref>", padLength)
  str.addTagName("</Singleton>", padLength)



// Refined <: TypeTree
def dumpRefined(using quotes: Quotes)(refined: quotes.reflect.Refined, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpt: TypeTree = refined.tpt
  val refinements: List[Definition] = refined.refinements

  str.addTagName("<Refined>", padLength)
    dumpTypeTreeImpl(refined, str, padLength)

    str.addChildTagName("<tpt>", padLength)
      dumpTree(tpt, str, padLength)
    str.addChildTagName("</tpt>", padLength)

    str.addChildTagName("<refinements>", padLength)
      refinements.foreach(r => dumpDefinition(r, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</refinements>", padLength)
  str.addTagName("</Refined>", padLength)



// Applied <: TypeTree
def dumpApplied(using quotes: Quotes)(applied: quotes.reflect.Applied, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpt: TypeTree = applied.tpt
  val args: List[Tree] = applied.args

  str.addTagName("<Applied>", padLength)
    dumpTypeTreeImpl(applied, str, padLength)

    str.addChildTagName("<tpt>", padLength)
      dumpTree(tpt, str, padLength)
    str.addChildTagName("</tpt>", padLength)

    str.addChildTagName("<args>", padLength)
      args.foreach(a => dumpTree(a, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</args>", padLength)
  str.addTagName("</Applied>", padLength)



// Annotated <: TypeTree
def dumpAnnotated(using quotes: Quotes)(annotated: quotes.reflect.Annotated, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val arg: TypeTree = annotated.arg
  val annotation: Term = annotated.annotation

  str.addTagName("<Annotated>", padLength)
    dumpTypeTreeImpl(annotated, str, padLength)

    str.addChildTagName("<arg>", padLength)
      dumpTree(arg, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</arg>", padLength)

    str.addChildTagName("<annotation>", padLength)
      dumpTree(annotation, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</annotation>", padLength)
  str.addTagName("</Annotated>", padLength)



// MatchTypeTree <: TypeTree
def dumpMatchTypeTree(using quotes: Quotes)(matchTypeTree: quotes.reflect.MatchTypeTree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val bound: Option[TypeTree] = matchTypeTree.bound
  val selector: TypeTree = matchTypeTree.selector
  val cases: List[TypeCaseDef] = matchTypeTree.cases

  str.addTagName("<MatchTypeTree>", padLength)
    dumpTypeTreeImpl(matchTypeTree, str, padLength)

    str.addChildTagName("<bound>", padLength)
      bound.foreach(b => dumpTree(b, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</bound>", padLength)

    str.addChildTagName("<selector>", padLength)
      dumpTree(selector, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</selector>", padLength)

    str.addChildTagName("<cases>", padLength)
      cases.foreach(c => dumpTree(c, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</cases>", padLength)
  str.addTagName("</MatchTypeTree>", padLength)


// ByName <: TypeTree
def dumpByName(using quotes: Quotes)(byName: quotes.reflect.ByName, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val result: TypeTree = byName.result

  str.addTagName("<ByName>", padLength)
    dumpTypeTreeImpl(byName, str, padLength)

    str.addChildTagName("<result>", padLength)
      dumpTree(result, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</result>", padLength)
  str.addTagName("</ByName>", padLength)


// LambdaTypeTree <: TypeTree
def dumpLambdaTypeTree(using quotes: Quotes)(lambdaTypeTree: quotes.reflect.LambdaTypeTree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tparams: List[TypeDef] = lambdaTypeTree.tparams
  val body: Tree = lambdaTypeTree.body

  str.addTagName("<LambdaTypeTree>", padLength)
    dumpTypeTreeImpl(lambdaTypeTree, str, padLength)

    str.addChildTagName("<tparams>", padLength)
      tparams.foreach(p => dumpTree(p, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</tparams>", padLength)

    str.addChildTagName("<body>", padLength)
      dumpTree(body, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</body>", padLength)
  str.addTagName("</LambdaTypeTree>", padLength)


// TypeBind <: TypeTree
def dumpTypeBind(using quotes: Quotes)(typeBind: quotes.reflect.TypeBind, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = typeBind.name
  val body: Tree = typeBind.body

  str.addTagName("<TypeBind>", padLength)
    dumpTypeTreeImpl(typeBind, str, padLength)

    str.addChildTagName("name", name, padLength)

    str.addChildTagName("<body>", padLength)
      dumpTree(body, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</body>", padLength)
  str.addTagName("</TypeBind>", padLength)


// TypeBlock <: TypeTree
def dumpTypeBlock(using quotes: Quotes)(typeBlock: quotes.reflect.TypeBlock, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val aliases: List[TypeDef] = typeBlock.aliases
  val tpt: TypeTree = typeBlock.tpt

  str.addTagName("<TypeBlock>", padLength)
    dumpTypeTreeImpl(typeBlock, str, padLength)

    str.addChildTagName("<aliases>", padLength)
      aliases.foreach(a => dumpTree(a, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</aliases>", padLength)

    str.addChildTagName("<tpt>", padLength)
      dumpTree(tpt, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</tpt>", padLength)
  str.addTagName("</TypeBlock>", padLength)


//
// base TypeTree == TypeTree <: Tree
def dumpBaseTypeTree(using quotes: Quotes)(typeTree: quotes.reflect.TypeTree, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<TypeBlock>", padLength)
    dumpTypeTreeImpl(typeTree, str, padLength)
  str.addTagName("</TypeBlock>", padLength)


//
// base TypeTree == TypeTree <: Tree
def dumpTypeTreeImpl(using quotes: Quotes)(typeTree: quotes.reflect.TypeTree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpe: TypeRepr = typeTree.tpe

  dumpTreeImpl(typeTree, str, padLength)
  str.addChildTagName("<tpe>", padLength)
    dumpTypeRepr(tpe, str, padLength + 2 * indentPerLevel)
  str.addChildTagName("</tpe>", padLength)




// TypeBoundsTree <: Tree
def dumpTypeBoundsTree(using quotes: Quotes)(typeBoundsTree: quotes.reflect.TypeBoundsTree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpe: TypeBounds = typeBoundsTree.tpe
  val low: TypeTree   = typeBoundsTree.low
  val hi:  TypeTree   = typeBoundsTree.hi

  str.addTagName("<TypeBoundsTree>", padLength)
    dumpTreeImpl(typeBoundsTree, str, padLength)

    str.addChildTagName("<tpe>", padLength)
      dumpTypeRepr(tpe, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</tpe>", padLength)

    str.addChildTagName("<low>", padLength)
      dumpTree(low, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</low>", padLength)

    str.addChildTagName("<hi>", padLength)
      dumpTree(hi, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</hi>", padLength)
  str.addTagName("</TypeBoundsTree>", padLength)



// WildcardTypeTree <: Tree
def dumpWildcardTypeTree(using quotes: Quotes)(wildcardTypeTree: quotes.reflect.WildcardTypeTree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val tpe: TypeRepr = wildcardTypeTree.tpe

  str.addTagName("<WildcardTypeTree>", padLength)
    dumpTreeImpl(wildcardTypeTree, str, padLength)

    str.addChildTagName("<tpe>", padLength)
      dumpTypeRepr(tpe, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</tpe>", padLength)
  str.addTagName("</WildcardTypeTree>", padLength)


// CaseDef <: Tree
def dumpCaseDef(using quotes: Quotes)(caseDef: quotes.reflect.CaseDef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val pattern: Tree = caseDef.pattern
  val guard: Option[Term] = caseDef.guard
  val rhs: Term = caseDef.rhs

  str.addTagName("<CaseDef>", padLength)
    dumpTreeImpl(caseDef, str, padLength)

    str.addChildTagName("<pattern>", padLength)
      dumpTree(pattern, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</pattern>", padLength)

    str.addChildTagName("<guard>", padLength)
      guard.foreach(g => dumpTree(g, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</guard>", padLength)

    str.addChildTagName("<rhs>", padLength)
      dumpTree(rhs, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</rhs>", padLength)
  str.addTagName("</CaseDef>", padLength)


// TypeCaseDef <: Tree
def dumpTypeCaseDef(using quotes: Quotes)(typeCaseDef: quotes.reflect.TypeCaseDef, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val pattern: Tree = typeCaseDef.pattern
  val rhs: TypeTree = typeCaseDef.rhs

  str.addTagName("<CaseDef>", padLength)
    dumpTreeImpl(typeCaseDef, str, padLength)

    str.addChildTagName("<pattern>", padLength)
      dumpTree(pattern, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</pattern>", padLength)

    str.addChildTagName("<rhs>", padLength)
      dumpTree(rhs, str, padLength + 2 * indentPerLevel)
    str.addChildTagName("</rhs>", padLength)
  str.addTagName("</CaseDef>", padLength)


def dumpInferredTypeTree(using quotes: Quotes)(inferredTypeTree: quotes.reflect.TypeTree, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<InferredTypeTree>", padLength)
    dumpTypeTreeImpl(inferredTypeTree, str, padLength)
  str.addTagName("</InferredTypeTree>", padLength)


def dumpMemberDef(using quotes: Quotes)(memberDef: quotes.reflect.Tree, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = getByReflection(memberDef, "name").unwrapOption.asInstanceOf[String]
  val isDef: Any = getByReflection(memberDef, "isDef").unwrapOption
  val namedType: Any = getByReflection(memberDef, "namedType").unwrapOption
  val comment: Any = getByReflection(memberDef, "rawComment", "comment").unwrapOption

  str.addTagName("<MemberDef>", padLength)
    str.addChildTagName("name", name, padLength)
    dumpTreeImpl(memberDef, str, padLength)
    str.addChildTagName("isDef", isDef, padLength)
    str.addChildTagName("namedType", namedType, padLength)
    str.addChildTagName("comment", comment, padLength)
  str.addTagName("</MemberDef>", padLength)

