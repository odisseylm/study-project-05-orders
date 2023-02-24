package org.mvv.scala.tools.beans

import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.collection.Map as BaseMap
//
import java.lang.reflect.Field as JavaField
import java.lang.reflect.Method as JavaMethod
//
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.{ equalImpl, isOneOf, nnArray, beforeLast, beforeFirst }
import org.mvv.scala.tools.beans._Type.toPortableType




case class _Field (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  _type: _Type,
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :
  override def toString: String = s"Field '$name' : $_type$modifiersAsString"
  def withAddedModifiers(newModifiers: _Modifier*): _Field =
    this.copy(modifiers = this.modifiers ++ newModifiers)(internalValue)
  override def toKey: _FieldKey = _FieldKey(this)
  //override def fixResultingType(resultingClass: Class[?]): _Field = fixFieldType(resultingClass, this)



case class _FieldKey(fieldName: String)(field: Option[_Field] = None) :
  override def toString: String =
    val resultTypeStr = field .map(f => s": ${f._type}") .getOrElse("")
    s"$fieldName$resultTypeStr"

object _FieldKey :
  def apply(field: _Field): _FieldKey = new _FieldKey(field.name)(Option(field))
  def apply(fieldName: String): _FieldKey = new _FieldKey(fieldName)(None)
