package org.mvv.scala.tools.props



trait NamedValue[T] :
  def name: String
  def value: T


object NamedValue:
  def apply[T](name: String, value: T): NamedValue[T] = new SimpleNamedValue[T](name, value)


private case class SimpleNamedValue[T] (
  name: String,
  value: T,
  ) extends NamedValue[T] :
  override def toString: String = s"name=$value"
