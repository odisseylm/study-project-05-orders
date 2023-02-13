package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// Symbol <: AnyRef
def dumpSymbol(using quotes: Quotes)(symbol: quotes.reflect.Symbol, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  str.addTagName("<Symbol>", padLength)
    // TODO: add impl in dump format
    str.addChildTagName("ALL", symbolToString(symbol), padLength)
  str.addTagName("</Symbol>", padLength)


// type Flags
//def dumpFlags(using quotes: Quotes)(el: quotes.reflect.Flags, str: StringBuilder, nextPadLength: Int): Unit = {}

// type Position <: AnyRef
//def dumpPosition(using quotes: Quotes)(el: quotes.reflect.Position, str: StringBuilder, nextPadLength: Int): Unit = {}
// SourceFile <: AnyRef
//def dumpSourceFile(using quotes: Quotes)(el: quotes.reflect.SourceFile, str: StringBuilder, nextPadLength: Int): Unit = {}

enum SymbolDetails :
  case Base, List, Tree

extension (using quotes: Quotes)(symbol: quotes.reflect.Symbol)
  //noinspection ScalaUnusedSymbol
  def nonEmptySymbol: Boolean =
    import quotes.reflect.*
    symbol == Symbol.noSymbol


//noinspection NoTailRecursionAnnotation // there is no recursion at all
def symbolToString(using quotes: Quotes)(symbol: quotes.reflect.Symbol): String =
  symbolToString(using quotes)(symbol, SymbolDetails.Base)
//noinspection ScalaUnusedSymbol
def symbolToString(using quotes: Quotes)(symbol: quotes.reflect.Symbol, details: SymbolDetails*): String =
  import quotes.reflect.*
  val str = StringBuilder()

  if details.contains(SymbolDetails.Base) then
    val owner: Symbol = symbol.owner
    val maybeOwner: Symbol = symbol.maybeOwner
    val flags: Flags = symbol.flags
    val privateWithin: Option[TypeRepr] = symbol.privateWithin
    val protectedWithin: Option[TypeRepr] = symbol.protectedWithin
    val name: String = symbol.name
    val fullName: String = symbol.fullName
    val pos: Option[Position] = symbol.pos
    val docstring: Option[String] = symbol.docstring
    val annotations: List[Term] = symbol.annotations
    val isDefinedInCurrentRun: Boolean = symbol.isDefinedInCurrentRun
    val isLocalDummy: Boolean = symbol.isLocalDummy
    val isRefinementClass: Boolean = symbol.isRefinementClass
    val isAliasType: Boolean = symbol.isAliasType
    val isAnonymousClass: Boolean = symbol.isAnonymousClass
    val isAnonymousFunction: Boolean = symbol.isAnonymousFunction
    val isAbstractType: Boolean = symbol.isAbstractType
    val isClassConstructor: Boolean = symbol.isClassConstructor
    val isType: Boolean = symbol.isType
    val isTerm: Boolean = symbol.isTerm
    val isPackageDef: Boolean = symbol.isPackageDef
    val isClassDef: Boolean = symbol.isClassDef
    val isTypeDef: Boolean = symbol.isTypeDef
    val isValDef: Boolean = symbol.isValDef
    val isDefDef: Boolean = symbol.isDefDef
    val isBind: Boolean = symbol.isBind
    val isNoSymbol: Boolean = symbol.isNoSymbol
    val exists: Boolean = symbol.exists
    val isTypeParam: Boolean = symbol.isTypeParam

    if name.nonEmptyName then str.append(", name: ").append(name)
    if fullName.nonEmptyName then str.append(", fullName: ").append(fullName)

    if isNoSymbol then str.append(", isNoSymbol")
    if isType then str.append(", isType")
    if isTerm then str.append(", isTerm")
    if isDefinedInCurrentRun then str.append(", isDefinedInCurrentRun")

    if isLocalDummy then str.append(", isLocalDummy")
    if isRefinementClass then str.append(", isRefinementClass")
    if isAliasType then str.append(", isAliasType")
    if isAnonymousClass then str.append(", isAnonymousClass")
    if isAnonymousFunction then str.append(", isAnonymousFunction")
    if isAbstractType then str.append(", isAbstractType")
    if isClassConstructor then str.append(", isClassConstructor")
    if isPackageDef then str.append(", isPackageDef")
    if isClassDef then str.append(", isClassDef")
    if isTypeDef then str.append(", isTypeDef")
    if isValDef then str.append(", isValDef")
    if isDefDef then str.append(", isDefDef")
    if isBind then str.append(", isBind")
    if exists then str.append(", exists")
    if isTypeParam then str.append(", isTypeParam")


    if owner.nonEmptySymbol then str.append(", owner: ").append(owner)
    if maybeOwner.nonEmptySymbol then str.append(", maybeOwner: ").append(maybeOwner)
    pos.foreach(p => str.append(", pos: ").append(p) )
    docstring.foreach(ds => str.append(", docstring: ").append(ds) )

    val flagsList = activeFlags(flags)
    if isFlagsEmpty(flagsList) then str.append(", flags: ").append(flagsToString(flagsList))

    privateWithin.foreach( p => str.append(", privateWithin: ").append(p) )
    protectedWithin.foreach( p => str.append(", protectedWithin: ").append(p) )

    if annotations.nonEmpty then str.append(", annotations: ").append(annotations.map(a => treeName(a)))
  end if

  if details.contains(SymbolDetails.List) then
    val declaredFields: List[Symbol] = symbol.declaredFields
    val fieldMembers: List[Symbol] = symbol.fieldMembers
    val declaredMethods: List[Symbol] = symbol.declaredMethods
    val methodMembers: List[Symbol] = symbol.methodMembers
    val declaredTypes: List[Symbol] = symbol.declaredTypes
    val typeMembers: List[Symbol] = symbol.typeMembers
    val declarations: List[Symbol] = symbol.declarations
    val paramSymss: List[List[Symbol]] = symbol.paramSymss
    val allOverriddenSymbols: Iterator[Symbol] = symbol.allOverriddenSymbols
    val caseFields: List[Symbol] = symbol.caseFields
    val children: List[Symbol] = symbol.children

    if declaredFields.nonEmpty then str.append(", declaredFields: ").append(declaredFields.map(_.name))
    if fieldMembers.nonEmpty then str.append(", fieldMembers: ").append(fieldMembers.map(_.name))
    if declaredMethods.nonEmpty then str.append(", declaredMethods: ").append(declaredMethods.map(_.name))
    if methodMembers.nonEmpty then str.append(", methodMembers: ").append(methodMembers.map(_.name))
    if declaredTypes.nonEmpty then str.append(", declaredTypes: ").append(declaredTypes.map(_.name))
    if typeMembers.nonEmpty then str.append(", declaredFields: ").append(typeMembers.map(_.name))
    if declarations.nonEmpty then str.append(", declarations: ").append(declarations.map(_.name))
    if paramSymss.nonEmpty then str.append(", paramSymss: ").append(paramSymss.flatten.map(_.name))
    if allOverriddenSymbols.nonEmpty then str.append(", allOverriddenSymbols: ").append(allOverriddenSymbols.map(_.name))
    if caseFields.nonEmpty then str.append(", caseFields: ").append(caseFields.map(_.name))
    if children.nonEmpty then str.append(", children: ").append(children.map(_.name))
  end if

  // TODO: impl
  val primaryConstructor: Symbol = symbol.primaryConstructor
  val signature: Signature = symbol.signature
  val moduleClass: Symbol = symbol.moduleClass
  val companionClass: Symbol = symbol.companionClass
  val companionModule: Symbol = symbol.companionModule

  if details.contains(SymbolDetails.Tree) then
    // TODO: impl
    val tree: Tree = symbol.tree
    val typeRef: TypeRef = symbol.typeRef
    val termRef: TermRef = symbol.termRef
  end if

  str.toString()


//def flagsToStrings(using quotes: Quotes)(flags: quotes.reflect.Flags): List[String] =
def activeFlags(using quotes: Quotes)(flags: quotes.reflect.Flags): Map[String, quotes.reflect.Flags] =
  val allFlags: Map[String, quotes.reflect.Flags] = flagsMap
  allFlags.filter((_, f) => flags.is(f))

def flagsToString(using quotes: Quotes)(flags: Map[String, quotes.reflect.Flags]): String =
  flags.keys.mkString("Flags { ", ", ", " }")

def flagsToString(using quotes: Quotes)(flags: quotes.reflect.Flags): String =
  activeFlags(flags).map(_._1).mkString("Flags { ", ", ", " }")

def isFlagsEmpty(using quotes: Quotes)(flags: Map[String, quotes.reflect.Flags]) =
  flags.isEmpty || (flags.size == 1 && flags.contains("EmptyFlags"))


def flagsMap(using quotes: Quotes): Map[String, quotes.reflect.Flags] =
  import quotes.reflect.*
  val flagsMap: Map[String, Flags] = Map(
    "Abstract" -> Flags.Abstract, "Artifact" -> Flags.Artifact, "Case" -> Flags.Case,
    "CaseAccessor" -> Flags.CaseAccessor, "Contravariant" -> Flags.Contravariant, "Covariant" -> Flags.Covariant,
    "Deferred" -> Flags.Deferred, "EmptyFlags" -> Flags.EmptyFlags, "Enum" -> Flags.Enum,
    "Erased" -> Flags.Erased, "Exported" -> Flags.Exported, "ExtensionMethod" -> Flags.ExtensionMethod,
    "FieldAccessor" -> Flags.FieldAccessor, "Final" -> Flags.Final, "Given" -> Flags.Given,
    "HasDefault" -> Flags.HasDefault, "Implicit" -> Flags.Implicit, "Infix" -> Flags.Infix,
    "Inline" -> Flags.Inline, "Invisible" -> Flags.Invisible, "JavaDefined" -> Flags.JavaDefined,
    "JavaStatic" -> Flags.JavaStatic, "Lazy" -> Flags.Lazy, "Local" -> Flags.Local,
    "Macro" -> Flags.Macro, "Method" -> Flags.Method, "Module" -> Flags.Module,
    "Mutable" -> Flags.Mutable, "NoInits" -> Flags.NoInits, "Opaque" -> Flags.Opaque,
    "Open" -> Flags.Open, "Override" -> Flags.Override, "Package" -> Flags.Package,
    "Param" -> Flags.Param, "ParamAccessor" -> Flags.ParamAccessor, "Private" -> Flags.Private,
    "PrivateLocal" -> Flags.PrivateLocal, "Protected" -> Flags.Protected, "Scala2x" -> Flags.Scala2x,
    "Sealed" -> Flags.Sealed, "StableRealizable" -> Flags.StableRealizable, "Static" -> Flags.Static,
    "Synthetic" -> Flags.Synthetic, "Trait" -> Flags.Trait, "Transparent" -> Flags.Transparent,
  )
  flagsMap

extension (str: String)
  def nonEmptyName: Boolean =
    str.isEmpty || str == "<none>"
