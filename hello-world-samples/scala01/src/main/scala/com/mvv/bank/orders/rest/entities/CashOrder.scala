package com.mvv.bank.orders.rest.entities

import scala.beans.BeanProperty

case class CashOrder (
    @BeanProperty
    var id: Long | Null = null,
    @BeanProperty
    var nullableLongProp: Long | Null = null,
    @BeanProperty
    var optionLongProp1: Long,
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
