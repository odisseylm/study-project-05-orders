package com.mvv.bank.orders.rest.conversion

import org.mapstruct.{ Mapper, Mapping, ObjectFactory }
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

// temp TODO: find better way
import com.mvv.nullables.AnyCanEqualGivens.givenCanEqual_Type_Type
import com.mvv.nullables.AnyCanEqualGivens.givenCanEqual_Type_TypeNull
import com.mvv.nullables.AnyCanEqualGivens.givenCanEqual_TypeNull_Type
import com.mvv.nullables.AnyCanEqualGivens.givenCanEqual_TypeNull_TypeNull
import com.mvv.nullables.AnyRefCanEqualGivens.givenCanEqual_Type_Type
import com.mvv.nullables.AnyRefCanEqualGivens.givenCanEqual_Type_TypeNull
import com.mvv.nullables.AnyRefCanEqualGivens.givenCanEqual_TypeNull_Type
import com.mvv.nullables.AnyRefCanEqualGivens.givenCanEqual_TypeNull_TypeNull



abstract class CashOrderMapper extends AbstractOrderMapper:
  //@Mapping(source = "domainField", target = "dtoField")
  def toDto(source: DomainBaseOrder): DtoCashOrder

  //@Mapping
  // (source = "dtoField", target = "domainField")
  def toDomain(source: DtoCashOrder): DomainBaseOrder

  @ObjectFactory
  // fun <T : DomainBaseOrder> createDomainOrder(source: DtoBaseOrder): T =
  //def createDomainCashOrder[T <: DomainBaseOrder](source: DtoCashOrder): T =
  def createDomainCashOrder(source: DtoCashOrder): DomainBaseOrder =
    source.orderType match
      case DtoOrderType.STOP_ORDER   => DomainStopOrder.uninitialized()
      case DtoOrderType.LIMIT_ORDER  => DomainLimitOrder.uninitialized()
      case DtoOrderType.MARKET_ORDER => DomainMarketOrder.uninitialized()
