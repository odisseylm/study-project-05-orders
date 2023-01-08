package com.mvv.bank.orders.rest.conversion.impl

import com.mvv.bank.orders.domain.CashOrder as DomainCashOrder
import com.mvv.bank.orders.rest.entities.CashOrder as DtoCashOrder
import org.mapstruct.{Mapper, Mapping}


@Mapper(uses = Array(classOf[CurrencyMapper])) // now this annotation is ignored and similar one from java class is used
trait CashOrderMapper {
  @Mapping(source = "domainField", target = "dtoField")
  def toDto(source: DomainCashOrder): DtoCashOrder

  @Mapping(source = "dtoField", target = "domainField")
  def toDomain(source: DtoCashOrder): DomainCashOrder
}
