package com.mvv.scala.temp.tests.tasty

import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.*


case class _Class (_package: String, simpleName: String) :
  def fullName: String = _Class.fullName(_package, simpleName)
  val parents: mutable.ArrayBuffer[_Class] = mutable.ArrayBuffer()
  val declaredFields: mutable.Map[String, _Field] = mutable.Map()
  val declaredMethods: mutable.Map[_MethodKey, _Method] = mutable.Map()
  val fields: mutable.Map[String, _Field] = mutable.Map()
  val methods: mutable.Map[_MethodKey, _Method] = mutable.Map()

  override def toString: String = s"Class $fullName, fields: [${fields.mkString(",")}], methods: [${methods.mkString(",")}]"

object _Class :
  def fullName(_package: String, simpleClassName: String): String = s"$_package.$simpleClassName"


enum _Modifier :
  case FieldAccessor, ParamAccessor, ExtensionMethod, Transparent, Macro, Static


private enum _Visibility :
  case Public, Other


private trait _ClassMember :
  val name: String
  val visibility: _Visibility
  val modifiers: Set[_Modifier]

private case class _Field (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  _type: String,
  )(
  // for debugging only
  val internalValue: Any
  ) extends _ClassMember :
  override def toString: String = s"Field '$name' : $_type (modifiers: $modifiers)"


// it is not used right now
private class _Param (
  val name: String,
  val _type: String,
)


private case class _Method (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  resultType: String,
  // scala has to much different kinds of params, for that reason we do not collect them
  hasParams: Boolean,
  )(
  // for debugging only
  val internalValue: Any
  ) extends _ClassMember:
  override def toString: String = s"Method { $name : $resultType, has params: $hasParams }"


case class _MethodKey private (signature: String)
object _MethodKey :
  def apply(method: _Method): _MethodKey =
    _MethodKey(s"${method.name}:${method.resultType}:${method.hasParams}") // TODO: use better than 'hasParams'
extension (m: _Method)
  def toKey: _MethodKey = _MethodKey(m)


def mergeAllDeclaredMembers(_class: _Class): Unit =
  val aa = _class.parents.map(_.fullName)
  println(s"merge $aa")
  _class.parents.reverse.foreach(p /*: _Class*/ =>
    _class.fields.addAll(p.declaredFields)
    _class.methods.addAll(p.declaredMethods)
  )
  _class.fields.addAll(_class.declaredFields)
  _class.methods.addAll(_class.declaredMethods)
  println(_class.declaredMethods)

