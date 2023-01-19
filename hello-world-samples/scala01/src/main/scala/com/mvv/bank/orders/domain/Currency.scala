package com.mvv.bank.orders.domain

import scala.language.strictEquality

import com.mvv.utils.isNull


//noinspection TypeCheckCanBeMatch
inline def toTypeOrNull[T](v: Any|Null): T|Null =
  if v.isInstanceOf[T] then v.asInstanceOf[T] else null

//inline def ifTypeOf[T, R](v: AnyRef|Null)(action: T=>R): R =
//  if v.isInstanceOf[T] then action(v.asInstanceOf[T]) else null

class Currency private (val value: String) /* extends AnyVal */ /* extends CanEqual[Currency, Currency] */ derives CanEqual {
  validateCurrency(value)

  override def toString: String = value
  //noinspection ScalaWeakerAccess
  infix def canEqual(other: Any|Null): Boolean = /*(other != null) &&*/ other.isInstanceOf[Currency]
  // for Scala 2.x
  //override def equals(other: Any): Boolean = other /*.asMatchable*/ match {
  //  case that: Currency => (that canEqual this) && this.value == that.value
  //  case _ => false
  //}
  // for Scala 3.x
  override def equals(other: Any|Null): Boolean =
    val that = toTypeOrNull[Currency](other)
    (that != null) && that.canEqual(this) && this.value == that.value

  override def hashCode(): Int = value.hashCode
}

//given CanEqual[Currency, Currency] = CanEqual.derived
given CanEqual[Currency|Null, Null] = CanEqual.derived
given CanEqual[Null, Currency|Null] = CanEqual.derived

//given CanEqual[Any|Null, Null] = CanEqual.derived
//given CanEqual[Null, Any|Null] = CanEqual.derived
//given CanEqual[AnyRef|Null, Null] = CanEqual.derived
//given CanEqual[Null, AnyRef|Null] = CanEqual.derived


object Currency :
  def apply(currency: String): Currency = of(currency)
  //def unapply(currency: Currency): Option[Currency] = Option(currency)
  def of(value: String): Currency = new Currency(value)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  def valueOf(value: String): Currency = Currency(value)

private def validateCurrency(currency: String|Null): Unit = {
  if (isNull(currency) || currency.nn.isBlank())
    throw IllegalArgumentException("Currency cannot be null or blank.")

  // T O D O: add other validation
}
