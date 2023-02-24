package org.mvv.scala.tools.quotes

import scala.quoted.Quotes



extension (using q: Quotes)(el: q.reflect.Tree)
  def isExprStatement: Boolean =
    import q.reflect.*
    el match
      case _: Apply => true
      case _: If => true
      case _: While => true
      case _: Match => true
      case _: Block => true
      case _: Try => true
      case _: Literal => true
      case _: Constant => true
      case _: Constant => true
      case _: Constant => true
      case _: Unapply => true
      case _: Assign => true
      case _: Closure => true
      //case _: Lambda => true
      case _: Return => true
      case _: Inlined => true
      case _ => false
