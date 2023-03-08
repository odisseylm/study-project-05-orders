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



enum SetPropValueMode :
  case
      /** Set property value using generated/user-defined property setter method (like 'myProp_='). */
      ByFieldAccessorMethod
      /** Set property value using assign ('=') operator. */
    , ByAssign



trait WritableProp[T] extends ReadOnlyProp[T] :
  def value_=(v: T): Unit


object WritableProp :
  def apply[T](name: String, _value: => T, _valueSetter: T => Unit): WritableProp[T] =
    WritablePropImpl[T](name, _value, _valueSetter)


private class WritablePropImpl[T] (
  name: String,
  _value: => T,
  _valueSetter: T => Unit,
  ) extends NamedCallableValue[T](name, _value), WritableProp[T] :
  override def value_=(v: T): Unit = _valueSetter(v)
