package org.mvv.scala.tools.quotes

import org.mvv.scala.tools.Logger

import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ tryDo, isSingleItemList, getByReflection }


enum SymbolDetails :
  case Base, List, Tree


extension (using quotes: Quotes)(symbol: quotes.reflect.Symbol)
  //noinspection ScalaUnusedSymbol
  def nonEmptySymbol: Boolean =
    import quotes.reflect.Symbol
    symbol == Symbol.noSymbol



def classSymbolDetails(using q: Quotes)(fullClassName: String): String =
  import q.reflect.Symbol
  try
    // strange... requesting non-existent class by Symbol.classSymbol/requiredClass
    // causes compilation error even if it is wrapped by try/catch
    // for that reason we need to verify that this class exists
    val classSymbol = if classExists(fullClassName) then Symbol.classSymbol(fullClassName) else Symbol.noSymbol
    symbolDetailsToString(classSymbol)
  catch case _: Throwable =>
    s"$fullClassName => no symbol"



def symbolBaseToString(using q: Quotes)(symbol: q.reflect.Symbol): String =
  symbolToString(using q)(symbol, SymbolDetails.Base)

def symbolDetailsToString(using q: Quotes)(symbol: q.reflect.Symbol): String =
  symbolToString(using q)(symbol, SymbolDetails.Base, SymbolDetails.List, SymbolDetails.Tree)

//noinspection ScalaUnusedSymbol
def symbolToString(using q: Quotes)(symbol: q.reflect.Symbol, details: SymbolDetails*): String =
  import q.reflect.*
  val str = StringBuilder()

  if details.contains(SymbolDetails.Base) then
    val owner: Option[Symbol] = tryDo { symbol.owner }
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


    if owner.isDefined && owner.get.nonEmptySymbol then str.append(", owner: ").append(owner)
    if maybeOwner.nonEmptySymbol then str.append(", maybeOwner: ").append(maybeOwner)
    pos.foreach(p => str.append(", pos: ").append(p) )
    docstring.foreach(ds => str.append(", docstring: ").append(ds) )

    val flagsList = activeFlagEntries(symbol)
    if !isFlagsEmpty(flagsList) then str.append(", flags: ").append(flagsToString(flagsList))

    privateWithin.foreach( p => str.append(", privateWithin: ").append(p) )
    protectedWithin.foreach( p => str.append(", protectedWithin: ").append(p) )

    if annotations.nonEmpty then str.append(", annotations: ")
      .append(annotations.map(a => treeName(a)))
  end if

  if details.contains(SymbolDetails.List) then
    val declaredFields: List[Symbol] = tryDo{ symbol.declaredFields }.getOrElse(Nil)
    val fieldMembers: List[Symbol] = tryDo{ symbol.fieldMembers }.getOrElse(Nil)
    val declaredMethods: List[Symbol] = tryDo{ symbol.declaredMethods }.getOrElse(Nil)
    val methodMembers: List[Symbol] = tryDo{ symbol.methodMembers }.getOrElse(Nil)
    val declaredTypes: List[Symbol] = tryDo{ symbol.declaredTypes }.getOrElse(Nil)
    val typeMembers: List[Symbol] = tryDo{ symbol.typeMembers }.getOrElse(Nil)
    val declarations: List[Symbol] = tryDo{ symbol.declarations }.getOrElse(Nil)
    val paramSymss: List[List[Symbol]] = tryDo{ symbol.paramSymss }.getOrElse(Nil)
    val allOverriddenSymbols: Iterator[Symbol] = tryDo{ symbol.allOverriddenSymbols }.getOrElse(Nil.iterator)
    val caseFields: List[Symbol] = tryDo{ symbol.caseFields }.getOrElse(Nil)
    val children: List[Symbol] = tryDo{ symbol.children }.getOrElse(Nil)

    if declaredFields.nonEmpty then str.append(",\n declaredFields: ").append(declaredFields.map(_.name))
    if fieldMembers.nonEmpty then str.append(",\n fieldMembers: ").append(fieldMembers.map(_.name))
    if declaredMethods.nonEmpty then str.append(",\n declaredMethods: ").append(declaredMethods.map(_.name))
    if methodMembers.nonEmpty then str.append(",\n methodMembers: ").append(methodMembers.map(_.name))
    if declaredTypes.nonEmpty then str.append(",\n declaredTypes: ").append(declaredTypes.map(_.name))
    if typeMembers.nonEmpty then str.append(",\n declaredFields: ").append(typeMembers.map(_.name))
    if declarations.nonEmpty then str.append(",\n declarations: ").append(declarations.map(_.name))
    if paramSymss.nonEmpty then str.append(",\n paramSymss: ").append(paramSymss.flatten.map(_.name))
    if allOverriddenSymbols.nonEmpty then str.append(",\n allOverriddenSymbols: ").append(allOverriddenSymbols.map(_.name))
    if caseFields.nonEmpty then str.append(",\n caseFields: ").append(caseFields.map(_.name))
    if children.nonEmpty then str.append(",\n children: ").append(children.map(_.name))
  end if

  // T O D O: impl
  val primaryConstructor: Symbol = symbol.primaryConstructor
  val signature: Signature = symbol.signature
  val moduleClass: Symbol = symbol.moduleClass
  val companionClass: Symbol = symbol.companionClass
  val companionModule: Symbol = symbol.companionModule

  if details.contains(SymbolDetails.Tree) then
    // T O D O: impl
    val tree: Option[Tree] = tryDo{ symbol.tree }
    val typeRef: Option[TypeRef] = tryDo{ symbol.typeRef }
    val termRef: Option[TermRef] = tryDo{ symbol.termRef }

    tree.foreach(t => str.append(",\n tree: ").append(t))
    typeRef.foreach(t => str.append(",\n typeRef: ").append(t))
    termRef.foreach(t => str.append(",\n termRef: ").append(t))
  end if

  str.toString()


def flagsToSet(using quotes: Quotes)(flags: quotes.reflect.Flags): Set[quotes.reflect.Flags] =
  allPossibleFlags.filter((_, f) => flags.is(f)).map(_._2).toSet
def flagsToEntries(using quotes: Quotes)(flags: quotes.reflect.Flags): List[(String, quotes.reflect.Flags)] =
  allPossibleFlags.filter((_, f) => flags.is(f))

def activeFlags(using quotes: Quotes)(symbol: quotes.reflect.Symbol): Set[quotes.reflect.Flags] =
  flagsToSet(symbol.flags)
def activeFlagEntries(using quotes: Quotes)(symbol: quotes.reflect.Symbol): List[(String, quotes.reflect.Flags)] =
  flagsToEntries(symbol.flags)


def flagsToString(using quotes: Quotes)(flags: IterableOnce[(String, quotes.reflect.Flags)]): String =
  flags.iterator.map(_._1).mkString("Flags { ", ", ", " }")
//noinspection NoTailRecursionAnnotation // there is no recursion
def flagsToString(using quotes: Quotes)(flags: quotes.reflect.Flags): String =
  flagsToString(flagsToEntries(flags))

private def isFlagsEmpty(using quotes: Quotes)(flags: List[(String, quotes.reflect.Flags)]) =
  flags.isEmpty || (flags.isSingleItemList && flags.head._2 == quotes.reflect.Flags.EmptyFlags)


def allPossibleFlags(using quotes: Quotes): List[ (String, quotes.reflect.Flags) ] =
  import quotes.reflect.*
  val flagEntries: List[(String, Flags)] = List(
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
  flagEntries

extension (str: String)
  def isEmptyName: Boolean =
    str.isEmpty || str == "<none>"
  def nonEmptyName: Boolean = !str.isEmptyName


def treeName(using quotes: Quotes)(tree: quotes.reflect.Tree): String =
  import quotes.reflect.Ident
  val name: String =
    tryDo { tree.asInstanceOf[Ident].name }
    .orElse( tryDo { getByReflection(tree, "name", "getName").asInstanceOf[String] } )
    .orElse( tryDo { tree.symbol.name } ) .getOrElse("?name?")
  name
