package com.mvv.bank.orders.domain

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class CashOrder {
  @BeanProperty
  var id: Long|Null = null
  @BeanProperty
  var nullableLongProp: Long | Null = null
  @BeanProperty
  var optionLongProp1: Option[Long] = None
  @BeanProperty
  var buyCurrency: Currency = uninitialized
  @BeanProperty
  var sellCurrency: Currency = uninitialized
  @BeanProperty
  var volume: BigDecimal = uninitialized
  @BeanProperty
  var limitPrice: Amount = uninitialized
  @BeanProperty
  var domainField: String = uninitialized
}
