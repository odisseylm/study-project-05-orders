package com.mvv.bank.orders.rest.conversion

import org.mapstruct.{ Mapper, Mapping, ObjectFactory, BeanMapping, MappingTarget, InheritConfiguration }
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

  /*  @InheritConfiguration is not used because now it allows ONLY to specify FULL configuration.
   *  If we write
   *   {{{
   *    @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
   *    @Mapping(target = "stopPrice", ignore = true)
   *    @Mapping(target = "limitPrice", ignore = true)
   *    @Mapping(target = "dailyExecutionType", ignore = true)
   *    def baseOrderAttrsToDto(source: DomainLimitOrder, @MappingTarget target: DtoCashOrder): Unit
   *   }}}
   *  We will need to repeat needed fields manually (it just kills main use of MapStruct :-( )
   *   {{{
   *    @InheritConfiguration(name = "baseOrderAttrsToDto")
   *    @Mapping(target = "limitPrice", source = "limitPrice")                 // ?? manually ??
   *    @Mapping(target = "dailyExecutionType", source = "dailyExecutionType") // ?? manually ??
   *    def limitOrderToDto(source: DomainLimitOrder): DtoCashOrder
   *   }}}
   *
   *  If we use method with default empty body as template it is just not ignored
   *   {{{
   *    @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
   *    def baseOrderAttrsToDto(source: DomainLimitOrder, @MappingTarget target: DtoCashOrder): Unit = {}
   *   }}}
   */


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



  // ***************************************************************************************************

  @Mapping(target = "log", ignore = true) // not property at all
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "orderType", ignore = true)     // read-only
  @Mapping(target = "priceCurrency", ignore = true) // read-only
  @BeanMapping(ignoreUnmappedSourceProperties = Array(
    // not used for LimitOrder
    "stopPrice",
    // read-only
    "orderType", "priceCurrency",
  ))
  def limitOrderToDomain(source: DtoCashOrder): DomainLimitOrder

  @Mapping(target = "log", ignore = true) // not property at all
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "orderType", ignore = true)     // read-only
  @Mapping(target = "priceCurrency", ignore = true) // read-only
  @BeanMapping(ignoreUnmappedSourceProperties = Array(
    // not used for StopOrder
    "limitPrice",
    // read-only
    "orderType", "priceCurrency"))
  def stopOrderToDomain(source: DtoCashOrder): DomainStopOrder

  @Mapping(target = "log", ignore = true) // not property at all
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "orderType", ignore = true)     // read-only
  @Mapping(target = "priceCurrency", ignore = true) // read-only
  @BeanMapping(ignoreUnmappedSourceProperties = Array(
    // not used for MarketOrder
    "limitPrice", "stopPrice", "dailyExecutionType",
    // read-only
    "orderType", "priceCurrency",
  ))
  def marketOrderToDomain(source: DtoCashOrder): DomainMarketOrder

  @ObjectFactory
  // fun <T : DomainBaseOrder> createDomainOrder(source: DtoBaseOrder): T =
  //def createDomainCashOrder[T <: DomainBaseOrder](source: DtoCashOrder): T =
  def createDomainCashOrder(source: DtoCashOrder): DomainBaseOrder =
    source.orderType match
      case DtoOrderType.STOP_ORDER   => DomainStopOrder.uninitialized()
      case DtoOrderType.LIMIT_ORDER  => DomainLimitOrder.uninitialized()
      case DtoOrderType.MARKET_ORDER => DomainMarketOrder.uninitialized()
