package com.mvv.scala.macros

import scala.annotation.nowarn


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
  def apply[PropertyType, OwnerType](propName: String, propValue: PropertyType|Null, propOwner: Class[OwnerType]|Null): PropValue[PropertyType, OwnerType] =
    new PropValueImpl(propName, toOption(propValue), toOption(propOwner))
  def apply[PropertyType, OwnerType](propName: String, propValue: Option[PropertyType], propOwner: Class[OwnerType]|Null): PropValue[PropertyType, OwnerType] =
    new PropValueImpl(propName, toOption(propValue), toOption(propOwner))
  // !!! Do not use default param with macros (it generates really bad code with temporary variables for defaults)
  def apply[PropertyType, OwnerType](propName: String, propValue: PropertyType|Null): PropValue[PropertyType, OwnerType] =
    apply[PropertyType, OwnerType](propName, propValue, null)
  def apply[PropertyType, OwnerType](propName: String, propValue: Option[PropertyType]): PropValue[PropertyType, OwnerType] =
    apply[PropertyType, OwnerType](propName, toOption(propValue), null)


@nowarn("msg=cannot be checked at runtime")
//noinspection TypeCheckCanBeMatch
private def toOption[T](@nowarn v: Any): Option[T] =
  if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]] else Option[T](v.asInstanceOf[T])




/*
// currently owner will be Any (probably later I'll manage to set it properly)
class ReadonlyProp[PropertyType, OwnerType] protected (
  val propName: String,
  def value: ()=>PropertyType,
  val propOwner: Option[Class[OwnerType]],
  )
*/



