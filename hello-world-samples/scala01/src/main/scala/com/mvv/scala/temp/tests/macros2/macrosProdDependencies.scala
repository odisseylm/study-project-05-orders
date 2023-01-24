package com.mvv.scala.macros

import scala.annotation.nowarn
import scala.reflect.ClassTag


trait PropValue[PropertyType, OwnerType] :
 def propName: String
 def value: Option[PropertyType]
  // currently owner will be Any (probably later I'll find how to fill it properly)
  def propOwner: Option[Class[OwnerType]]


private class PropValueImpl[PropertyType, OwnerType] (
  val propName: String,
  val value: Option[PropertyType],
  val propOwner: Option[Class[OwnerType]],
) extends PropValue[PropertyType, OwnerType]


object PropValue :
  def apply[T, O](propName: String, propValue: T|Null, propOwner: Class[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), toOption(propOwner))
  def apply[T, O](propName: String, propValue: Option[T], propOwner: Class[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), toOption(propOwner))

  // Seems it is impossible to get class of processing/compiling now class
  // for that reason hack with ClassTag is used
  def apply[T, O](propName: String, propValue: T|Null, propOwner: ClassTag[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), classTagToClass(propOwner))
  def apply[T, O](propName: String, propValue: Option[T], propOwner: ClassTag[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), classTagToClass(propOwner))

  // !!! Do not use default param with macros (it generates really bad code with temporary variables for defaults)
  def apply[P, O](propName: String, propValue: P|Null): PropValue[P, O] =
    new PropValueImpl(propName, toOption(propValue), None)
  def apply[T, O](propName: String, propValue: Option[T]): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), None)


private def classTagToClass[T](classTag: ClassTag[T]|Null): Option[Class[T]] =
  toOption[ClassTag[T]](classTag).map(_.runtimeClass.asInstanceOf[Class[T]])

@nowarn("msg=cannot be checked at runtime")
//noinspection TypeCheckCanBeMatch,DuplicatedCode,IsInstanceOf
private def toOption[T](@nowarn v: T|Null|Option[T]): Option[T] =
  if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]] else Option[T](v.asInstanceOf[T])




/*
// currently owner will be Any (probably later I'll manage to set it properly)
class ReadonlyProp[PropertyType, OwnerType] protected (
  val propName: String,
  def value: ()=>PropertyType,
  val propOwner: Option[Class[OwnerType]],
  )
*/



