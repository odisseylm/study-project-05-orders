package com.mvv.scala.temp.tests.tasty

import com.mvv.scala.temp.tests.tasty._Type.toPortableType

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
  case FieldAccessor, CustomFieldAccessor, JavaPropertyAccessor, ParamAccessor, ExtensionMethod, Transparent, Macro, Static


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
  resultType: _Type,
  mainParams: List[_Type],
  // scala has to much different kinds of params, for that reason we do not collect all them
  hasExtraScalaParams: Boolean,
  )(
  // for debugging only
  val internalValue: Any
  ) extends _ClassMember :

  //println(name)
  if (name.startsWith("getInterfaceValue11")) {
    println(name)
  }

  val isScalaPropertyAccessor: Boolean = modifiers.contains(_Modifier.FieldAccessor)
    || modifiers.contains(_Modifier.CustomFieldAccessor)
  val isPropertyAccessor: Boolean = isScalaPropertyAccessor || modifiers.contains(_Modifier.JavaPropertyAccessor)

  if isPropertyAccessor then
    require(!hasExtraScalaParams, "Property accessor cannot have additional params.")
    require(mainParams.size <= 1, s"Property accessor cannot have ${mainParams.size} params.")

  override def toString: String =
    //if (name.startsWith("getInterfaceValue11")) {
    //  println(name)
    //}
    val extraSuffix = if hasExtraScalaParams then ", hasExParams" else ""
    s"Method { $name (${mainParams.mkString(",")}) $extraSuffix }"


@nowarn("msg=cannot be checked at runtime")
private inline def equalImpl[T <: Equals](thisV: T, other: Any|Null)(inline comparing: (T, T)=>Boolean): Boolean =
  //import com.mvv.nullables.AnyCanEqualGivens.given
  import scala.language.unsafeNulls
  if other == null || !other.isInstanceOf[T] then false
  else comparing(thisV, other.asInstanceOf[T])


class _Type (val typeName: String) extends Equals derives CanEqual :
  override def toString: String = typeName
  override def hashCode: Int = this.toPortableType.typeName.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[_Type]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: _Type => that.canEqual(this) && toPortableType(this.typeName) == toPortableType(that.typeName)
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { (v1, v2) => v1.toPortableType.typeName == v2.toPortableType.typeName }


object _Type :
  private val VoidTypeName = "void"
  private val UnitTypeName = "scala.Unit"

  private val VoidType: _Type = _Type(VoidTypeName)
  val UnitType: _Type = _Type(UnitTypeName)

  extension (t: _Type)
    def toPortableType: _Type = t.typeName match
      case VoidTypeName => UnitType
      case _ => t

    def isVoid: Boolean = { t == VoidType || t == UnitType }


case class _MethodKey private (methodName: String, params: List[_Type], hasExtraScalaParams: Boolean)(method: _Method) :
  override def toString: String =
    if (methodName.startsWith("getInterfaceValue11")) {
      println(methodName)
    }
    val extraSuffix = if hasExtraScalaParams then " ( hasExParams )" else ""
    val paramsStr = if method.isScalaPropertyAccessor && params.isEmpty then "" else s"(${params.mkString(",")})"
    val resultTypeStr = if method.resultType.isVoid then "" else s": ${method.resultType.toString}"
    s"$methodName$paramsStr$resultTypeStr$extraSuffix"

object _MethodKey :
  def apply(method: _Method): _MethodKey =
    new _MethodKey(method.name, method.mainParams, method.hasExtraScalaParams)(method)
extension (m: _Method)
  def toKey: _MethodKey = _MethodKey(m)


def mergeAllDeclaredMembers(_class: _Class): Unit =
  val aa = _class.parents.map(_.fullName)
  println(s"merge $aa")
  _class.parents.reverse.foreach(p /*: _Class*/ =>
    _class.fields.addWithKeyReplacement(p.declaredFields)
    _class.methods.addWithKeyReplacement(p.declaredMethods)
  )
  _class.fields.addWithKeyReplacement(_class.declaredFields)
  _class.methods.addWithKeyReplacement(_class.declaredMethods)
  println(_class.declaredMethods)


// replacing key is needed for having proper optional key metadata
// (it is optional but helps debugging)
extension [K,V](map: mutable.Map[K,V])
  private def addWithKeyReplacement(toBeReplacedOrAdded: mutable.Map[K,V]): Unit =
    toBeReplacedOrAdded.keys.foreach(k => map.remove(k))
    map.addAll(toBeReplacedOrAdded)
