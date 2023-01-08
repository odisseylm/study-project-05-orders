package com.mvv.bank.orders.domain

case class Amount(
    value: BigDecimal,
    currency: Currency,
  ) :
  override def toString: String = s"$value $currency"

object Amount :
  def of(value: BigDecimal, currency: Currency): Amount = Amount(value, currency)
