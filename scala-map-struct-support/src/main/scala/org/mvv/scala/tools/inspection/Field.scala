package org.mvv.scala.tools.inspection

import scala.annotation.{ nowarn, tailrec }
import scala.collection.{ mutable, Map as BaseMap }
import scala.compiletime.uninitialized
import scala.reflect.ClassTag
//
import java.lang.reflect.{ Field as JavaField, Method as JavaMethod }
//
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.{ equalImpl, isOneOf, nnArray, stripAfter }
import org.mvv.scala.tools.inspection._Type.toPortableType
import org.mvv.scala.tools.inspection.{ _Field, _FieldKey, _ClassMember, _Type }



case class _Field (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  _type: _Type,
  )(
    // noinspection ScalaUnusedSymbol , for debugging only
    val internalValue: Any|Null
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
