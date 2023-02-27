package org.mvv.scala.tools.quotes

import scala.quoted.{ Quotes, Type }



extension (using q: Quotes)(el: q.reflect.Tree)
  inline def isExprStatement: Boolean =
    import q.reflect.*
    el match
      case _: Apply => true
      case _: If => true
      case _: While => true
      case _: Match => true
      case _: Block => true
      case _: Try => true
      case _: Literal => true
      case _: Unapply => true
      case _: Assign => true
      case _: Closure => true
      //case _: Lambda => true
      case _: Return => true
      case _: Inlined => true
      case _ => false


  // It does not work (even when it is inline): the type test for q.reflect.BooleanConstant cannot be checked at runtime because it refers to an abstract type member or type parameter
  inline def isConstant: Boolean =
    import q.reflect.*
    el match
      case _: BooleanConstant => true
      case _: ByteConstant => true
      case _: ShortConstant => true
      case _: IntConstant => true
      case _: LongConstant => true
      case _: FloatConstant => true
      case _: DoubleConstant => true
      case _: CharConstant => true
      case _: StringConstant => true
      case _: UnitConstant => true
      case _: NullConstant => true
      case _: ClassOfConstant => true
      case _: Constant => true
      case _ => false
