package com.mvv.bank.orders.rest.conversion

import org.mapstruct.{
  Mapper, Mapping, ObjectFactory, BeanMapping, MappingTarget, InheritConfiguration, BeforeMapping
}
import com.mvv.bank.orders.domain.{
  createCashOrderFor as createDomainOrderFor,
  OrderType         as DomainOrderType,
  AbstractCashOrder as DomainBaseOrder,
  CashLimitOrder    as DomainLimitOrder,
  CashMarketOrder   as DomainMarketOrder,
  CashStopOrder     as DomainStopOrder,
}
import com.mvv.bank.orders.rest.entities.{
  BaseOrder as DtoBaseOrder,
  CashOrder as DtoCashOrder,
  OrderType as DtoOrderType,
}
import com.mvv.log.{ safe, underlyingSafe }
import com.mvv.nullables.{ isNotNull, ifNull }
import com.mvv.utils.require
import com.mvv.scala.mapstruct.getScalaMapStructMapper

import scala.annotation.unused



abstract class CashOrderMapper extends AbstractOrderMapper, Cloneable :

  /*  @InheritConfiguration is not used because now it allows ONLY to specify FULL configuration.
   *  If we write
   *   {{{
   *    @Mapping(target = "stopPrice", ignore = true)
   *    @Mapping(target = "limitPrice", ignore = true)
   *    @Mapping(target = "dailyExecutionType", ignore = true)
   *    @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
   *    def baseOrderAttrsToDto(source: DomainBaseOrder, @MappingTarget target: DtoCashOrder): Unit
   *   }}}
   *  We will need to repeat needed fields manually and @BeanMapping is not inherited
   *  (it just kills main use of MapStruct :-( )
   *   {{{
   *    @Mapping(target = "limitPrice", source = "limitPrice")                  // ?? manually again ??
   *    @Mapping(target = "dailyExecutionType", source = "dailyExecutionType")  // ?? manually again ??
   *    @InheritConfiguration("baseOrderAttrsToDto")
   *    @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote")) // Oops ?? It is not inherited !! Piece of sh!!!
   *    def limitOrderToDto(source: DomainLimitOrder): DtoCashOrder
   *   }}}
   *
   *
   *  If we use method with default empty body as template it is just not ignored at all :-(
   *   {{{
   *    @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
   *    def baseOrderAttrsToDto(source: DomainLimitOrder, @MappingTarget target: DtoCashOrder): Unit = {}
   *   }}}
   */

  @Mapping(target = "stopPrice", ignore = true)
  @Mapping(target = "limitPrice", ignore = true)
  @Mapping(target = "dailyExecutionType", ignore = true)
  @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
  def baseOrderAttrsToDto(source: DomainBaseOrder, @MappingTarget target: DtoCashOrder): Unit

  @Mapping(target = "limitPrice", source = "limitPrice")                  // ?? manually again ??
  @Mapping(target = "dailyExecutionType", source = "dailyExecutionType")  // ?? manually again ??
  @InheritConfiguration("baseOrderAttrsToDto")
  // Oops ?? It is not inherited !! Piece of sh!!!
  // see bug https://github.com/mapstruct/mapstruct/issues/2092
  @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
  def limitOrderToDto(source: DomainLimitOrder): DtoCashOrder

  @Mapping(target = "stopPrice", source = "stopPrice")                   // ?? manually again ??
  @Mapping(target = "dailyExecutionType", source = "dailyExecutionType") // ?? manually again ??
  @InheritConfiguration("baseOrderAttrsToDto")
  // Oops ?? It is not inherited !! Piece of sh!!!
  // see bug https://github.com/mapstruct/mapstruct/issues/2092
  @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
  def stopOrderToDto(source: DomainStopOrder): DtoCashOrder

  @InheritConfiguration("baseOrderAttrsToDto")
  // Oops ?? It is not inherited !! Piece of sh!!!
  // see bug https://github.com/mapstruct/mapstruct/issues/2092
  @BeanMapping(ignoreUnmappedSourceProperties = Array("log", "product", "resultingQuote"))
  def marketOrderToDto(source: DomainMarketOrder): DtoCashOrder

  def toDto(source: DomainBaseOrder): DtoCashOrder =
    source match
      case stopOrder: DomainStopOrder => stopOrderToDto(stopOrder)
      case limitOrder: DomainLimitOrder => limitOrderToDto(limitOrder)
      case marketOrder: DomainMarketOrder => marketOrderToDto(marketOrder)



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

  def toDomain(source: DtoCashOrder): DomainBaseOrder =
    source.orderType match
      case DtoOrderType.STOP_ORDER   => stopOrderToDomain(source)
      case DtoOrderType.LIMIT_ORDER  => limitOrderToDomain(source)
      case DtoOrderType.MARKET_ORDER => marketOrderToDomain(source)


  @BeforeMapping
  //noinspection ScalaUnusedSymbol
  def validateDomainOrderBeforeConverting(source: DomainBaseOrder, @MappingTarget target: DtoBaseOrder): Unit =
    // We do not perform strict validation to have possibility to see/analyze probably wrong order.
    // source.validateCurrentState()

    // But we will use simple validation to have better error message.
    // (error/exception will be thrown in any case)
    source.checkRequiredPropsAreInitialized()


  @BeforeMapping
  //noinspection ScalaUnusedSymbol
  def validateDtoOrderBeforeConverting(source: DtoBaseOrder, @MappingTarget target: Any): Unit =
    if source.orderType == DtoOrderType.MARKET_ORDER then
      require(source.limitPrice.isEmpty && source.stopPrice.isEmpty,
        s"Market price cannot have limit/stop price (${source.limitPrice.underlyingSafe}/${source.stopPrice.underlyingSafe}).")
      require(source.dailyExecutionType.isEmpty,
        s"Market price cannot have daily execution type (${source.dailyExecutionType.underlyingSafe}).")


  @ObjectFactory  @unused
  def createDomainCashOrder(source: DtoBaseOrder): DomainBaseOrder =
    // T O D O: please make it easier (without injecting EnumMappers) if you know ho to do it...
    val _enumMappers: EnumMappers = enumMappers.ifNull( getScalaMapStructMapper[EnumMappers]() )
    createDomainOrderFor(_enumMappers.OrderTypeToDomain(source.orderType))

  override def clone(): CashOrderMapper = super.clone().asInstanceOf[CashOrderMapper]
