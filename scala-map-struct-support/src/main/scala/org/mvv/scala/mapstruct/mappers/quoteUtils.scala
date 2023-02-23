package org.mvv.scala.mapstruct.mappers

import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ Logger, isImplClass }



//noinspection ScalaUnusedSymbol // TODO: move to other package
extension (using q: Quotes)(el: q.reflect.Tree)
  def isTyped: Boolean = el.isImplClass("Typed")
  def isApply: Boolean = el.isImplClass("Apply")
  def isSelect: Boolean = el.isImplClass("Select")
  def isTypeApply: Boolean  = el.isImplClass("TypeApply")
  def isSeqLiteral: Boolean = el.isImplClass("SeqLiteral")
