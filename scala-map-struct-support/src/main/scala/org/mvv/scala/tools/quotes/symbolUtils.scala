package org.mvv.scala.tools.quotes

import org.mvv.scala.tools.Logger

import scala.annotation.targetName
import scala.quoted.Quotes
//



extension (using q: Quotes)(symbol: q.reflect.Symbol)
  //noinspection ScalaUnusedSymbol
  def isEmptySymbol: Boolean =
    import q.reflect.Symbol
    symbol == Symbol.noSymbol // || symbol.name.isEmptySymbolName
  def nonEmptySymbol: Boolean = !symbol.isEmptySymbol



extension (str: String)
  def isEmptySymbolName: Boolean  = str.isEmpty || str == "<none>"
  def nonEmptySymbolName: Boolean = !str.isEmptySymbolName


extension (using q: Quotes)(symbolFlags: q.reflect.Flags)
  def isOneOf(flags: q.reflect.Flags*): Boolean =
    flags.exists(f => symbolFlags.is(f))


def activeSymbolFlags(using q: Quotes)(symbol: q.reflect.Symbol): Set[q.reflect.Flags] =
  activeSymbolFlagEntries(symbol).map(_._2).toSet
def activeSymbolFlagEntries(using q: Quotes)(symbol: q.reflect.Symbol): List[(String, q.reflect.Flags)] =
  val flags = symbol.flags
  allPossibleFlags.filter((_, f) => flags.is(f))


def areFlagsEmpty(using q: Quotes)(flags: Iterable[q.reflect.Flags]) =
  flags.isEmpty || (flags.sizeIs == 1 && flags.head == q.reflect.Flags.EmptyFlags)
def areFlagEntriesEmpty(using q: Quotes)(flags: List[(String, q.reflect.Flags)]) =
  flags.isEmpty || (flags.sizeIs == 1 && flags.head._2 == q.reflect.Flags.EmptyFlags)




def allPossibleFlags(using quotes: Quotes): List[ (String, quotes.reflect.Flags) ] =
  import quotes.reflect.Flags
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
