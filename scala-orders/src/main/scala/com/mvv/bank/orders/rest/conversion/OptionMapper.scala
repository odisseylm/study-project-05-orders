package com.mvv.bank.orders.rest.conversion

import org.mapstruct.Mapper


private def optionToValueOrNull[T](v: Option[T]): T =
  import scala.language.unsafeNulls
  val target = if (v == null || v.isEmpty) null else v.orNull
  target.asInstanceOf[T]

//noinspection ScalaUnusedSymbol
//@Mapper
trait OptionMapper :
  def mapOptionToLong(source: Option[Long]): Long = optionToValueOrNull(source)
  def mapOptionToInt(source: Option[Int]): Int = optionToValueOrNull(source)
  def mapOptionTo[T](source: Option[T]): T = optionToValueOrNull(source)
  // T O D O: add other required ones

  def mapLongToOption(source: Int): Option[Long] = Option(source)
  def mapLongToOption(source: Short): Option[Long] = Option(source)
  def mapLongToOption(source: Long): Option[Long] = Option(source)
  def mapToOption[T](source: T): Option[T] = Option(source)
  // T O D O: add other required ones
