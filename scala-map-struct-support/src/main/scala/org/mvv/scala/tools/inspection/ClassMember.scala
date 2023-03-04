package org.mvv.scala.tools.inspection

import scala.annotation.{ nowarn, tailrec }
import scala.collection.{ mutable, Map as BaseMap }
import scala.compiletime.uninitialized
import scala.reflect.ClassTag
//
import java.lang.reflect.{ Field as JavaField, Method as JavaMethod }
//
import org.mvv.scala.tools.CollectionsOps.{ asString, containsOneOf }
import org.mvv.scala.tools.inspection._Type.toPortableType
import org.mvv.scala.tools.{ equalImpl, isOneOf, nnArray, stripAfter }



//noinspection ScalaFileName
trait _ClassMember :
  val name: String
  val visibility: _Visibility
  val modifiers: Set[_Modifier]

  protected def modifiersAsString: String =
    if modifiers.nonEmpty then s" (modifiers: ${modifiers.asString})" else ""

  def withAddedModifiers(newModifiers: _Modifier*): _ClassMember
  def toKey: AnyRef
  // it is used for replacing generic type name (like A, T, etc) with runtime type
  // (in most cases it will be java.lang.Object)
  //def fixResultingType(resultingClass: Class[?]): _ClassMember



extension (m: _ClassMember)
  def isPublic: Boolean = m.visibility == _Visibility.Public
