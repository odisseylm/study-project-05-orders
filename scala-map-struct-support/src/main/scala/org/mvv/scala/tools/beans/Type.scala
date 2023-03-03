package org.mvv.scala.tools.beans

import org.mvv.scala.tools.KeepDelimiter

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
import org.mvv.scala.tools.{ equalImpl, isOneOf, nnArray, stripAfter, replacePrefix }
import org.mvv.scala.tools.KeepDelimiter.ExcludeDelimiter
import org.mvv.scala.tools.beans._Type.toPortableType



def typeNameToRuntimeClassName(typeName: String): String =
  // if it is generics
  typeName
    .replacePrefix("_root_.scala.", "scala.")
    .stripAfter("[", ExcludeDelimiter)



class _Type private (
  val declaredTypeName: String,
  val runtimeTypeName: String,
  ) extends Equals derives CanEqual :

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



object _Type :

  def apply(declaredTypeName: String, runtimeTypeName: String): _Type =
    val fixedDeclaredTypeName = declaredTypeName
      .replacePrefix("_root_.scala.", "scala.")
    //println(s"_Type($declaredTypeName, $runtimeTypeName)")
    if runtimeTypeName.startsWith("scala.Predef.String") then
      println(s"_Type($declaredTypeName, $runtimeTypeName)")
      throw IllegalStateException("Test error 1.")
    if runtimeTypeName.startsWith("_root_.scala.") then
      println(s"_Type($declaredTypeName, $runtimeTypeName)")
      throw IllegalStateException("Test error 2.")
    declaredTypeName match
      //case "scala.Predef.String" | "java.lang.String" => StringType
      case _ => new _Type(fixedDeclaredTypeName, runtimeTypeName)

  def apply(declaredTypeName: String): _Type =
    apply(declaredTypeName, typeNameToRuntimeClassName(declaredTypeName))


  val VoidTypeName = "void"
  /*private[_Type]*/
  val UnitTypeName = "scala.Unit" /*private[_Type]*/

  //noinspection ScalaWeakerAccess
  val VoidType: _Type = new _Type(VoidTypeName, VoidTypeName)
  val UnitType: _Type = new _Type(UnitTypeName, VoidTypeName)
  val ObjectType: _Type = new _Type("java.lang.Object", "java.lang.Object")
  val StringType: _Type = new _Type("java.lang.String", "java.lang.String")

  extension (_type: Class[?])
    def isBool: Boolean = _type.isOneOf(classOf[Boolean], java.lang.Boolean.TYPE, classOf[java.lang.Boolean])
    def isVoid: Boolean = _type.isOneOf(Void.TYPE, classOf[Unit])



  extension (t: _Type)
    private def toTypeName: String = t.runtimeTypeName //.getOrElse(t.declaredTypeName)

    def toPortableType: _Type = t.declaredTypeName match
      case VoidTypeName => UnitType
      case _ => t

    def isVoid: Boolean =
      t.toTypeName.isOneOf("void", "Void", "Unit", "scala.Unit")

    def isBool: Boolean =
      t.toTypeName.isOneOf("boolean", "Boolean", "java.lang.Boolean", "scala.Boolean")
