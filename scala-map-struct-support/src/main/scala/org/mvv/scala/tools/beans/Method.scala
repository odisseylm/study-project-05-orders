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
import org.mvv.scala.tools.{ equalImpl, isOneOf, nnArray, stripAfter, uncapitalize }
import org.mvv.scala.tools.beans._Type.toPortableType



case class _Method (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  returnType: _Type,
  mainParams: List[_Type],
  // scala has to much different kinds of params, for that reason we do not collect all them
  hasExtraScalaParams: Boolean,
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :

  validate()

  private def validate(): Unit =
    if modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor, _Modifier.JavaPropertyAccessor) then
      require(!hasExtraScalaParams, "Property accessor cannot have additional params.")
      require(mainParams.size <= 1, s"Property accessor cannot have ${mainParams.size} params.")

  override def toString: String =
    val extraSuffix = if hasExtraScalaParams then ", hasExParams" else ""
    s"Method { $name (${mainParams.mkString(",")}) $extraSuffix }"

  def withAddedModifiers(newModifiers: _Modifier*): _Method =
    this.copy(modifiers = this.modifiers ++ newModifiers)(internalValue)
  override def toKey: _MethodKey = _MethodKey(this)
  //override def fixResultingType(resultingClass: Class[?]): _Method = fixMethodType(resultingClass, this)



case class _MethodKey (methodName: String, params: List[_Type], hasExtraScalaParams: Boolean)(method: Option[_Method] = None) :
  override def toString: String =
    //noinspection MapGetOrElseBoolean
    val isScalaPropertyAccessor = method .map(_.isScalaPropertyAccessor) .getOrElse(false)
    val returnType = method .map(_.returnType) .getOrElse(_Type.UnitType)

    val extraSuffix = if hasExtraScalaParams then " ( hasExParams )" else ""
    val paramsStr = if isScalaPropertyAccessor && params.isEmpty then "" else s"(${params.mkString(",")})"
    val resultTypeStr = if returnType.isVoid then "" else s": $returnType"
    s"$methodName$paramsStr$resultTypeStr$extraSuffix"

object _MethodKey :
  def apply(method: _Method): _MethodKey =
    new _MethodKey(method.name, method.mainParams, method.hasExtraScalaParams)(Option(method))
  // seems default param value does not work as I expect
  def apply(methodName: String, params: List[_Type], hasExtraScalaParams: Boolean): _MethodKey =
    new _MethodKey(methodName, params, hasExtraScalaParams)(None)
  //def getter[T](name: String)(implicit ct: ClassTag[T]): _MethodKey = apply(name, List(_Type(ct.runtimeClass.toString)), false)
  def getter(propName: String): _MethodKey = apply(propName, Nil, false)
  def setter[T](propName: String)(implicit ct: ClassTag[T]): _MethodKey = apply(s"${propName}_=", List(_Type(ct.toString)), false)



extension (m: _Method)
  def isScalaPropertyAccessor: Boolean =
    m.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor)
  def isJavaPropertyAccessor: Boolean =
    m.modifiers.containsOneOf(_Modifier.JavaPropertyAccessor)
  def isPropertyAccessor: Boolean = m.isScalaPropertyAccessor || m.isJavaPropertyAccessor

  def isGetterMethod: Boolean = m.isPropertyAccessor && m.mainParams.isEmpty && !m.returnType.isVoid
  def isSetterMethod: Boolean =
    m.isPropertyAccessor && m.mainParams.sizeIs == 1

  def toPropName: String = m match
    case javaPropM if javaPropM.modifiers.containsOneOf(_Modifier.JavaPropertyAccessor) =>
      javaPropM.name match
        case setMName if setMName.startsWith("set") => setMName.stripPrefix("set").uncapitalize
        case isMName  if isMName.startsWith("is")  => isMName.stripPrefix("is").uncapitalize
        case getMName if getMName.startsWith("get") => getMName.stripPrefix("get").uncapitalize
        case other => other
    case scalaPropM if scalaPropM.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor) =>
      scalaPropM.name match
        case setMName if setMName.endsWith("_=") => setMName.stripSuffix("_=")
        case setMName if setMName.endsWith("_$eq") => setMName.stripSuffix("_$eq")
        case other => other
    case other => other.name
