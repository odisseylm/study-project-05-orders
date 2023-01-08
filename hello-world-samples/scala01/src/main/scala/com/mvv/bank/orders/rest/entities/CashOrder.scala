package com.mvv.bank.orders.rest.entities

import scala.beans.BeanProperty

case class CashOrder (
    @BeanProperty
    var id: Long | Null = null,
    @BeanProperty
    var buyCurrency: String,
    @BeanProperty
    var sellCurrency: String,
    @BeanProperty
    var volume: BigDecimal,
    @BeanProperty
    var limitPrice: Amount,
    @BeanProperty
    var dtoField: String,
  )
