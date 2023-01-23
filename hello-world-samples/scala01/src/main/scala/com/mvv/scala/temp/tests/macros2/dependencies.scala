package com.mvv.scala.temp.tests.macros2

import scala.annotation.nowarn
import scala.compiletime.uninitialized


@nowarn("msg=cannot be checked at runtime")
//noinspection TypeCheckCanBeMatch
private def toOption[T](@nowarn v: Any): Option[T] =
  if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]] else Option[T](v.asInstanceOf[T])

trait BeanPropertyValue[PropertyType, OwnerType] :
  val name: String
  val value: Option[PropertyType]

object BeanPropertyValue :
  def beanPropertyValue[PropertyType, OwnerType](
    name: String, value: PropertyType): BeanPropertyValue[PropertyType, OwnerType] =
      SimpleBeanPropertyValue[PropertyType, OwnerType](name, toOption[PropertyType](value))

private case class SimpleBeanPropertyValue[PropertyType, OwnerType] (
  name: String, value: Option[PropertyType]) extends BeanPropertyValue[PropertyType, OwnerType]


case class BeanPropValue(name: String, value: Any|Null)


trait BeanProperty[PropertyType, OwnerType] :
  val name: String
  val getter: Option[OwnerType => PropertyType]
  val setter: Option[(OwnerType,PropertyType) => Unit]
  val ownerType: Option[Class[OwnerType]]

object BeanProperty :
  def beanProperty[PropertyType, OwnerType](
    name: String,
    getter: OwnerType => PropertyType = noGetter[PropertyType, OwnerType](),
    setter: (OwnerType, PropertyType) => Unit = noSetter[PropertyType, OwnerType](),
    ownerType: Class[OwnerType] = classOf[Nothing],
    ): BeanProperty[PropertyType, OwnerType] =
    SimpleBeanProperty[PropertyType, OwnerType](name, Option(getter), Option(setter), Option(ownerType))

private case class SimpleBeanProperty[PropertyType, OwnerType] (
  name: String,
  getter: Option[OwnerType => PropertyType],
  setter: Option[(OwnerType, PropertyType) => Unit],
  ownerType: Option[Class[OwnerType]]
) extends BeanProperty[PropertyType, OwnerType]


private inline def noGetter[PropertyType, OwnerType]() = _noGetter.asInstanceOf[OwnerType => PropertyType]
private inline def noSetter[PropertyType, OwnerType]() = _noSetter.asInstanceOf[(OwnerType, PropertyType) => Unit]

private val _noGetter: Any => Any = throw IllegalArgumentException("Getter not implemented.")
private val _noSetter: (Any, Any) => Unit = throw IllegalArgumentException("Setter not implemented.")
