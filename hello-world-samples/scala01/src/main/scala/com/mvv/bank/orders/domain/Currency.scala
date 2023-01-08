package com.mvv.bank.orders.domain


class Currency private (val value: String) /* extends AnyVal */ /* extends CanEqual[Currency, Currency] */ derives CanEqual {
  validateCurrency(value)

  override def toString: String = value
  //noinspection ScalaWeakerAccess
  def canEqual(other: Any): Boolean = other.isInstanceOf[Currency]
  override def equals(other: Any): Boolean = other match {
    case that: Currency => (that canEqual this) && this.value == that.value
    case _ => false
  }

  override def hashCode(): Int = value.hashCode
}

//given CanEqual[Currency, Currency] = CanEqual.derived

object Currency :
  def of(value: String): Currency = Currency(value)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  def valueOf(value: String): Currency = Currency(value)

private def validateCurrency(currency: String|Null): Unit = {
  if (currency == null || currency.isBlank())
    throw IllegalArgumentException("Currency cannot be null or blank.")

  // T O D O: add other validation
}
