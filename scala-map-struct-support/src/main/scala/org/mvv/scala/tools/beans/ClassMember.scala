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



//noinspection ScalaFileName
trait _ClassMember :
  val name: String
  val visibility: _Visibility
  val modifiers: Set[_Modifier]
  def withAddedModifiers(newModifiers: _Modifier*): _ClassMember
  def toKey: AnyRef
  // it is used for replacing generic type name (like A, T, etc) with runtime type
  // (in most cases it will be java.lang.Object)
  //def fixResultingType(resultingClass: Class[?]): _ClassMember



extension (m: _ClassMember)
  private def isPublic: Boolean = m.visibility == _Visibility.Public
