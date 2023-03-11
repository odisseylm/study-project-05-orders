package com.mvv.bank.orders.rest.conversion

import org.mapstruct.{ Mapper, Mapping, ObjectFactory, BeanMapping }
import com.mvv.bank.orders.domain.{
  AbstractCashOrder as DomainBaseOrder,
  CashLimitOrder    as DomainLimitOrder,
  CashMarketOrder   as DomainMarketOrder,
  CashStopOrder     as DomainStopOrder,
}
import com.mvv.bank.orders.rest.entities.{
  CashOrder as DtoCashOrder,
  OrderType as DtoOrderType,
}



abstract class CashOrderMapper extends AbstractOrderMapper :

  @Mapping(target = "stopPrice", ignore = true)
  @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
  def limitOrderToDto(source: DomainLimitOrder): DtoCashOrder

  @Mapping(target = "limitPrice", ignore = true)
  @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
  def stopOrderToDto(source: DomainStopOrder): DtoCashOrder

  @Mapping(target = "stopPrice", ignore = true)
  @Mapping(target = "limitPrice", ignore = true)
  @Mapping(target = "dailyExecutionType", ignore = true)
  @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
  def marketOrderToDto(source: DomainMarketOrder): DtoCashOrder

  @Mapping(target = "log", ignore = true) // not property at all
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "orderType", ignore = true)     // read-only
  @Mapping(target = "priceCurrency", ignore = true) // read-only
  @BeanMapping(ignoreUnmappedSourceProperties = Array("orderType", "stopPrice", "priceCurrency"))
  def limitOrderToDomain(source: DtoCashOrder): DomainLimitOrder

  @ObjectFactory
  // fun <T : DomainBaseOrder> createDomainOrder(source: DtoBaseOrder): T =
  //def createDomainCashOrder[T <: DomainBaseOrder](source: DtoCashOrder): T =
  def createDomainCashOrder(source: DtoCashOrder): DomainBaseOrder =
    source.orderType match
      case DtoOrderType.STOP_ORDER   => DomainStopOrder.uninitialized()
      case DtoOrderType.LIMIT_ORDER  => DomainLimitOrder.uninitialized()
      case DtoOrderType.MARKET_ORDER => DomainMarketOrder.uninitialized()
