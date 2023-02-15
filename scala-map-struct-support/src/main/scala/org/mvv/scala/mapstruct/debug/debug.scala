package org.mvv.scala.mapstruct.debug

import scala.quoted.Quotes
//
import org.mvv.scala.mapstruct.Logger


private val log = Logger("org.mvv.scala.mapstruct.debug")


//noinspection ScalaUnusedSymbol
// Debug functions
// temp
def printFields(label: String, obj: Any): Unit =
  log.info(s"\n\n$label   $obj")
  import scala.language.unsafeNulls
  //noinspection TypeCheckCanBeMatch // scala3 warns about non matchable type
  if obj .isInstanceOf [List[?]] then
    obj.asInstanceOf[List[Any]].zipWithIndex
      .foreach { case (el, i) => printFields(s"$label $i:", _) }
  else
    allMethods(obj).foreach( printField(obj.getClass.getSimpleName, obj, _) )


def printField(label: String, obj: Any, prop: String): Unit =
  try { log.info(s"$label.$prop: ${ getProp(obj, prop) }") } catch { case _: Exception => }


//noinspection ScalaUnusedSymbol
def allMethods(obj: Any): List[String] =
  import scala.language.unsafeNulls
  obj.getClass.getMethods .map(_.getName) .toList


//noinspection ScalaUnusedSymbol
def allMethodsDetail(obj: Any): String =
  import scala.language.unsafeNulls
  obj.getClass.getMethods /*.map(_.getName)*/ .toList .mkString("\n")


def getProp(obj: Any, method: String): Any = {
  import scala.language.unsafeNulls
  val methodMethod = try { obj.getClass.getDeclaredMethod(method) } catch { case _: Exception => obj.getClass.getMethod(method) }
  val v = methodMethod.invoke(obj)
  //noinspection TypeCheckCanBeMatch
  if (v.isInstanceOf[Iterator[Any]]) {
    v.asInstanceOf[Iterator[Any]].toList
  } else v
}


def printTreeSymbolInfo(using quotes: Quotes)(tree: quotes.reflect.Tree): Unit =
  org.mvv.scala.mapstruct.toSymbol(tree).foreach(s => printSymbolInfo(s))

def printSymbolInfo(using quotes: Quotes)(symbol: Option[quotes.reflect.Symbol]): Unit =
  symbol.foreach(s => printSymbolInfo(s))

def printSymbolInfo(using quotes: Quotes)(symbol: quotes.reflect.Symbol): Unit =
  import quotes.reflect.*

  var str = ""
  str += s"name: ${symbol.name}\n"
  if symbol.privateWithin.isDefined then str += s"privateWithin: ${symbol.privateWithin}\n"
  if symbol.protectedWithin.isDefined then str += s"protectedWithin: ${symbol.protectedWithin}\n"
  str += s"fullName: ${symbol.fullName}\n"
  if symbol != Symbol.noSymbol then
    str += s"tree: ${symbol.tree}\n"

  if symbol.isType then str += s"isType: ${symbol.isType}\n"
  if symbol.isTerm then str += s"isTerm: ${symbol.isTerm}\n"
  if symbol.isAliasType then str += s"isAliasType: ${symbol.isAliasType}\n"

  if symbol.isDefinedInCurrentRun then str += s"isDefinedInCurrentRun: ${symbol.isDefinedInCurrentRun}\n"
  if symbol.isLocalDummy then str += s"isLocalDummy: ${symbol.isLocalDummy}\n"
  if symbol.isRefinementClass then str += s"isRefinementClass: ${symbol.isRefinementClass}\n"
  if symbol.isAnonymousClass then str += s"isAnonymousClass: ${symbol.isAnonymousClass}\n"
  if symbol.isAnonymousFunction then str += s"isAnonymousFunction: ${symbol.isAnonymousFunction}\n"
  if symbol.isAbstractType then str += s"isAbstractType: ${symbol.isAbstractType}\n"
  if symbol.isClassConstructor then str += s"isClassConstructor: ${symbol.isClassConstructor}\n"
  if symbol.isPackageDef then str += s"isPackageDef: ${symbol.isPackageDef}\n"
  if symbol.isClassDef then str += s"isClassDef: ${symbol.isClassDef}\n"
  if symbol.isTypeDef then str += s"isTypeDef: ${symbol.isTypeDef}\n"
  if symbol.isValDef then str += s"isValDef: ${symbol.isValDef}\n"
  if symbol.isDefDef then str += s"isDefDef: ${symbol.isDefDef}\n"
  if symbol.isBind then str += s"isBind: ${symbol.isBind}\n"
  if symbol.isNoSymbol then str += s"isNoSymbol: ${symbol.isNoSymbol}\n"
  if symbol.exists then str += s"exists: ${symbol.exists}\n"
  if symbol.isTypeParam then str += s"isTypeParam: ${symbol.isTypeParam}\n"

  str += s"typeRef: ${symbol.typeRef}\n"
  str += s"termRef: ${symbol.termRef}\n"

  if symbol.annotations.nonEmpty then str += s"annotations: ${symbol.annotations}\n"
  if symbol.declaredFields.nonEmpty then str += s"declaredFields: ${symbol.declaredFields}\n"
  if symbol.fieldMembers.nonEmpty then str += s"fieldMembers: ${symbol.fieldMembers}\n"
  if symbol.declaredMethods.nonEmpty then str += s"declaredMethods: ${symbol.declaredMethods}\n"
  if symbol.methodMembers.nonEmpty then str += s"methodMembers: ${symbol.methodMembers}\n"
  if symbol.declaredTypes.nonEmpty then str += s"declaredTypes: ${symbol.declaredTypes}\n"
  if symbol.typeMembers.nonEmpty then str += s"typeMembers: ${symbol.typeMembers}\n"
  if symbol.declarations.nonEmpty then str += s"declarations: ${symbol.declarations}\n"
  if symbol.paramSymss.nonEmpty then str += s"paramSymss: ${symbol.paramSymss}\n"
  if symbol.allOverriddenSymbols.nonEmpty then str += s"allOverriddenSymbols: ${symbol.allOverriddenSymbols}\n"
  if symbol.caseFields.nonEmpty then str += s"caseFields: ${symbol.caseFields}\n"
  if symbol.children.nonEmpty then str += s"children: ${symbol.children}\n"

  str += s"primaryConstructor: ${symbol.primaryConstructor}\n"
  str += s"signature: ${symbol.signature}\n"
  str += s"moduleClass: ${symbol.moduleClass}\n"
  str += s"companionClass: ${symbol.companionClass}\n"
  str += s"companionModule: ${symbol.companionModule}\n"

  println(s"Symbol details\n$str")
