package com.mvv.bank.orders.rest.entities


case class Amount (
    value: BigDecimal,
    currency: String,
  ) :
  override def toString: String = s"$value $currency"

object Amount :
  def of(value: BigDecimal, currency: String): Amount = Amount(value, currency)
