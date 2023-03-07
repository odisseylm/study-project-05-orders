package com.mvv.bank.orders.rest.entities

import com.mvv.bank.orders.domain.Currency

import javax.annotation.concurrent.Immutable
import javax.annotation.{Tainted, Untainted}
import scala.annotation.meta.{field, getter, param}


@Untainted @Immutable
case class Amount (
    value: BigDecimal,
    @(Tainted @param) @(Untainted @field @getter)
    currency: String,
  ) derives CanEqual :
  Currency(currency) // validating
  @Untainted override def toString: String = s"$value $currency"
