package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// ParamClause <: AnyRef
// TermParamClause <: ParamClause
def dumpTermParamClause(using quotes: Quotes)(tp: quotes.reflect.TermParamClause, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val params: List[ValDef] = tp.params
  val isImplicit: Boolean = tp.isImplicit
  val isGiven: Boolean = tp.isGiven
  val isErased: Boolean = tp.isErased

  str.addTagName("<TermParamClause>", padLength)
    str.addChildTagName("isImplicit", isImplicit, padLength)
    str.addChildTagName("isGiven", isGiven, padLength)
    str.addChildTagName("isErased", isErased, padLength)

    str.addChildTagName("<params>", padLength)
    params.foreach(vd => dumpValDef(vd, str, padLength + 2 * indentPerLevel))
    str.addChildTagName("</params>", padLength)
  str.addTagName("</TermParamClause>", padLength)



// TypeParamClause <: ParamClause
def dumpTypeParamClause(using quotes: Quotes)(tpc: quotes.reflect.TypeParamClause, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val params: List[TypeDef] = tpc.params

  str.addTagName("<TypeParamClause>", padLength)
  params.foreach(td => dumpTypeDef(td, str, padLength + 2 * indentPerLevel))
  str.addTagName("</TypeParamClause>", padLength)



// base ParamClause = ParamClause <: AnyRef
def dumpParamClause(using quotes: Quotes)(p: quotes.reflect.ParamClause, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val params: List[ValDef] | List[TypeDef] = p.params

  str.addTagName("<ParamClause>", padLength)
  params.foreach(pp => pp match
    //case vd if vd.isValDef => dumpValDef(vd.asInstanceOf[ValDef], str, padLength + 2 * indentPerLevel)
    case vd if vd.isValDef => dumpTree(vd.asInstanceOf[ValDef], str, padLength + 2 * indentPerLevel)
    case td if td.isTypeDef => dumpTypeDef(td.asInstanceOf[TypeDef], str, padLength + 2 * indentPerLevel)
    case _ => log.warn(s"Unexpected param clause [$pp]")
  )
  str.addTagName("</ParamClause>", padLength)


