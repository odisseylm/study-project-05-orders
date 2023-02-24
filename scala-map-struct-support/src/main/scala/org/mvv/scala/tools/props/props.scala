package org.mvv.scala.tools.props



trait NamedValue[T] :
  def name: String
  def value: T


object NamedValue:
  def apply[T](name: String, value: T): NamedValue[T] = new NamedStaticValue[T](name, value)


private case class NamedStaticValue[T] (
  name: String,
  value: T,
  ) extends NamedValue[T] :
  override def toString: String = s"name=$value"




trait ReadOnlyProp[T] extends NamedValue[T]

object ReadOnlyProp :
  def apply[T](name: String, value: => T): ReadOnlyProp[T] = NamedCallableValue(name, value)



private class NamedCallableValue[T] (
  val name: String,
  _value: => T,
  ) extends ReadOnlyProp[T], NamedValue[T] :
  override def value: T = _value
  override def toString: String = s"name=$value"
