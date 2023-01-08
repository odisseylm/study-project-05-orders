package com.mvv.bank.orders.domain

import scala.beans.BeanProperty

class CashOrder {
  @BeanProperty
  var id: Long|Null = null
  @BeanProperty
  var buyCurrency: Currency = _
  @BeanProperty
  var sellCurrency: Currency = _
  @BeanProperty
  var volume: BigDecimal = _
  @BeanProperty
  var limitPrice: Amount = _
  @BeanProperty
  var domainField: String = _
}
