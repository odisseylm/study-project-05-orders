package com.mvv.bank.orders.rest.conversion.impl

import com.mvv.bank.orders.domain.{
  Amount as DomainAmount,
  Currency as DomainCurrency,
}
import com.mvv.bank.orders.rest.entities.Amount as DtoAmount
import org.mapstruct.Mapper



@Mapper // now this annotation is ignored and similar one from java class is used
trait CurrencyMapper {
  def toDto(source: DomainCurrency): String = source.toString()
  def toDomain(source: String): DomainCurrency = DomainCurrency.of(source)
}


@Mapper(uses = Array(classOf[CurrencyMapper])) // now this annotation is ignored and similar one from java class is used
trait AmountMapper {
  def toDto(source: DomainAmount): DtoAmount = DtoAmount.of(source.value, source.currency.value)
  def toDomain(source: DtoAmount): DomainAmount = DomainAmount.of(source.value, DomainCurrency.of(source.currency))
}
