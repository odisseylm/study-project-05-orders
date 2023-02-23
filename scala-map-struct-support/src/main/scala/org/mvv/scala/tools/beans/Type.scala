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



def typeNameToRuntimeClassName(typeName: String): String =
  // if it is generics
  typeName.beforeFirst('[').getOrElse(typeName)



class _Type (
  val declaredTypeName: String,
  val runtimeTypeName: String,
  ) extends Equals derives CanEqual :

  def this(declaredTypeName: String) = this(declaredTypeName, typeNameToRuntimeClassName(declaredTypeName))

  def withRuntimeType(newRuntimeTypeName: String): _Type = _Type(this.declaredTypeName, newRuntimeTypeName)
  def withRuntimeType(newRuntimeType: Class[?]): _Type = withRuntimeType(newRuntimeType.getName.nn)
  override def toString: String =
    if runtimeTypeName != declaredTypeName then s"$declaredTypeName/$runtimeTypeName" else declaredTypeName

  override def hashCode: Int =
    val portable = this.toPortableType
    31 * portable.declaredTypeName.hashCode + portable.runtimeTypeName.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[_Type]

  // in scala3 it causes warning "pattern selector should be an instance of Matchable"
  //override def equals(other: Any): Boolean = other match
  //  case that: _Type => that.canEqual(this) && toPortableType(this.typeName) == toPortableType(that.typeName)
  //  case _ => false
  override def equals(other: Any): Boolean =
    // 'equalImpl' is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { (v1, v2) =>
      v1.toPortableType.declaredTypeName == v2.toPortableType.declaredTypeName &&
      v1.toPortableType.runtimeTypeName  == v2.toPortableType.runtimeTypeName
    }
end _Type


object Types :
  val VoidTypeName = "void"        /*private[_Type]*/
  val UnitTypeName = "scala.Unit"  /*private[_Type]*/

  //noinspection ScalaWeakerAccess
  val VoidType: _Type = _Type(VoidTypeName)
  val UnitType: _Type = _Type(UnitTypeName)
  val ObjectType: _Type = _Type("java.lang.Object")
  val StringType: _Type = _Type("java.lang.String")

  extension (_type: Class[?])
    def isBool: Boolean = _type.isOneOf(classOf[Boolean], java.lang.Boolean.TYPE, classOf[java.lang.Boolean])
    def isVoid: Boolean = _type.isOneOf(Void.TYPE, classOf[Unit])




object _Type :
  import Types.*

  extension (t: _Type)
    private def toTypeName: String = t.runtimeTypeName //.getOrElse(t.declaredTypeName)

    def toPortableType: _Type = t.declaredTypeName match
      case VoidTypeName => UnitType
      case _ => t

    def isVoid: Boolean =
      t.toTypeName.isOneOf("void", "Void", "Unit", "scala.Unit")

    def isBool: Boolean =
      t.toTypeName.isOneOf("boolean", "Boolean", "java.lang.Boolean", "scala.Boolean")
