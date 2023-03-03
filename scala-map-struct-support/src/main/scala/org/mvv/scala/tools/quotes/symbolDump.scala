package org.mvv.scala.tools.quotes

import org.mvv.scala.tools.Logger

import scala.annotation.targetName
import scala.quoted.Quotes
//
import org.mvv.scala.tools.tryDo
import org.mvv.scala.tools.quotes.{ activeSymbolFlagEntries, areFlagEntriesEmpty }



enum SymbolDetails :
  case Base, List, Tree



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
    str
      .appendStrAttr(symbol.name, "name")
      .appendStrAttr(symbol.fullName, "fullName")

      .appendIsAttr(symbol.isDefinedInCurrentRun, "isDefinedInCurrentRun")
      .appendIsAttr(symbol.isLocalDummy, "isLocalDummy")
      .appendIsAttr(symbol.isRefinementClass, "isRefinementClass")
      .appendIsAttr(symbol.isAliasType, "isAliasType")
      .appendIsAttr(symbol.isAnonymousClass, "isAnonymousClass")
      .appendIsAttr(symbol.isAnonymousFunction, "isAnonymousFunction")
      .appendIsAttr(symbol.isAbstractType, "isAbstractType")
      .appendIsAttr(symbol.isClassConstructor, "isClassConstructor")
      .appendIsAttr(symbol.isType, "isType")
      .appendIsAttr(symbol.isTerm, "isTerm")
      .appendIsAttr(symbol.isPackageDef, "isPackageDef")
      .appendIsAttr(symbol.isClassDef, "isClassDef")
      .appendIsAttr(symbol.isTypeDef, "isTypeDef")
      .appendIsAttr(symbol.isValDef, "isValDef")
      .appendIsAttr(symbol.isDefDef, "isDefDef")
      .appendIsAttr(symbol.isBind, "isBind")
      .appendIsAttr(symbol.isNoSymbol, "isNoSymbol")
      .appendIsAttr(symbol.exists, "exists")
      .appendIsAttr(symbol.isTypeParam, "isTypeParam")
      .appendIsAttr(symbol.isNoSymbol, "isNoSymbol")

      .appendSymbol(symbol.owner, "owner")
      .appendSymbol(symbol.maybeOwner, "maybeOwner")
      .appendAttr(symbol.docstring.get, "docstring")

      .appendAttr(symbol.pos.get, "pos")

    tryDo { activeSymbolFlagEntries(symbol) }
      .filter(flags => !areFlagEntriesEmpty(flags))
      .map(flags => flags.map(_._1).mkString("[", ", ", "]")  )
      .foreach { str.appendStrAttr(_, "flags") }

    str.appendTypeRepr(symbol.privateWithin.get, "privateWithin")
    str.appendTypeRepr(symbol.protectedWithin.get, "protectedWithin")

    str.appendAttr(symbol.annotations.map(an => getSimpleClassName(an.tpe.show)).mkString(","), "annotations")
  end if

  if details.contains(SymbolDetails.List) then
    str
      .appendNonEmptyNames(symbol.declaredFields, "declaredFields")
      .appendNonEmptyNames(symbol.declaredMethods, "declaredMethods")
      .appendNonEmptyNames(symbol.declaredTypes, "declaredTypes")
      .appendNonEmptyNames(symbol.declarations, "declarations")

      .appendNonEmptyNames(symbol.fieldMembers, "fieldMembers")
      .appendNonEmptyNames(symbol.caseFields, "caseFields")
      .appendNonEmptyNames(symbol.methodMembers, "methodMembers")
      .appendNonEmptyNames(symbol.allOverriddenSymbols, "allOverriddenSymbols")
      .appendNonEmptyNames(symbol.typeMembers, "typeMembers")
      .appendNonEmptyNames(symbol.children, "children")
      .appendNonEmptyNames(symbol.paramSymss.flatten, "paramSymss")
  end if

  str
    .appendSymbol(symbol.primaryConstructor, "primaryConstructor")
    .appendAttr(symbol.signature, "signature")
    .appendSymbol(symbol.moduleClass, "moduleClass")
    .appendSymbol(symbol.companionClass, "companionClass")
    .appendSymbol(symbol.companionModule, "companionModule")

  if details.contains(SymbolDetails.Tree) then
    str
      .appendAttrOnNewLine(symbol.tree, "tree")
      .appendAttrOnNewLine(symbol.typeRef, "typeRef")
      .appendAttrOnNewLine(symbol.termRef, "termRef")
  end if

  str.toString()



extension (using q: Quotes)(str: StringBuilder)
  private def appendNonEmptyNames
    (symbols: IterableOnce[q.reflect.Symbol], label: String): StringBuilder =
    if symbols.iterator.nonEmpty then
      if str.nonEmpty then str.append(",\n")
      str.append(" ").append(label).append(": ").append(symbols.iterator.map(s => tryDo(s.name).getOrElse("?Unknown?")).mkString(", "))
    str


  private def appendIsAttr(isAttr: => Boolean, isAttrName: => String): StringBuilder =
    tryDo(isAttr).filter(_ == true)
      .foreach { _ =>
        if str.nonEmpty then str.append(", ")
        str.append(isAttrName)
      }
    str


  private def appendStrAttr(strAttr: => String, label: => String): StringBuilder =
    tryDo(strAttr).filter(_.nonEmptySymbolName)
      .foreach { s =>
        if str.nonEmpty then str.append(", ")
        str.append(label).append(": ").append(s)
      }
    str


  private def appendTypeRepr(typeRepr: => q.reflect.TypeRepr, label: String): StringBuilder =
    str.appendStrAttr(typeRepr.show, label)


  private def appendAttr(child: => Any, label: String): StringBuilder =
    tryDo(child).foreach { ss =>
      if str.nonEmpty then str.append(", ")
      val asStr = tryDo(child.toString).getOrElse("?Unknown?")
      str.append(label).append(": ").append(asStr) }
    str


  private def appendAttrOnNewLine(child: => Any, label: String): StringBuilder =
    tryDo(child).foreach { ss =>
      if str.nonEmpty then str.append(",\n")
      val asStr = tryDo(child.toString).getOrElse("?Unknown?")
      str.append(label).append(": ").append(asStr) }
    str


  private def appendSymbol(child: => q.reflect.Symbol, label: String): StringBuilder =
    str.appendAttr(child.toString, label)
