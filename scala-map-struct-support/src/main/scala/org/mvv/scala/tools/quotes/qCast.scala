package org.mvv.scala.tools.quotes

import scala.quoted.Quotes



/** Just helpers to minimize code and have real type verification
 *  since 'asInstanceOf' does NOT do any type validation for path-dependent types.
 */
extension (using q: Quotes)(tree: q.reflect.Tree)
  def asClassDef: q.reflect.ClassDef =
    tree match
      case cd: q.reflect.ClassDef => cd
      case _ => throw ClassCastException(s"Error of casting ${tree.getClass.nn.getName} to ClassDef (tree: $tree).")

  def asValDef: q.reflect.ValDef =
    tree match
      case vd: q.reflect.ValDef => vd
      case _ => throw ClassCastException(s"Error of casting ${tree.getClass.nn.getName} to ValDef (tree: $tree).")

  def asDefDef: q.reflect.DefDef =
    tree match
      case dd: q.reflect.DefDef => dd
      case _ => throw ClassCastException(s"Error of casting ${tree.getClass.nn.getName} to DefDef (tree: $tree).")
