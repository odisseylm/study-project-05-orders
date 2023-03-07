package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.domain.{
  BuySellType as DomainBuySellType,
  DailyExecutionType as DomainDailyExecutionType,
  OrderType as DomainOrderType,
  OrderState as DomainOrderState,
  Side as DomainSide,
}
import com.mvv.bank.orders.rest.entities.{
  BuySellType as DtoBuySellType,
  DailyExecutionType as DtoDailyExecutionType,
  OrderState as DtoOrderState,
  OrderType as DtoOrderType,
  Side as DtoSide,
}
import org.mapstruct.Mapper
import org.mvv.scala.mapstruct.mappers.enumMappingFunc



def aaa() = {
  val orderTypeToDto: (com.mvv.bank.orders.domain.OrderType => com.mvv.bank.orders.rest.entities.OrderType) = enumMappingFunc[com.mvv.bank.orders.domain.OrderType, com.mvv.bank.orders.rest.entities.OrderType]()
}


//noinspection ScalaUnusedSymbol,ScalaFileName
//@Mapper
trait EnumMappers :
  //def orderTypeToDto1: (com.mvv.bank.orders.domain.OrderType => com.mvv.bank.orders.rest.entities.OrderType) = enumMappingFunc[com.mvv.bank.orders.domain.OrderType, com.mvv.bank.orders.rest.entities.OrderType]()
  //def orderTypeToDto2(source: com.mvv.bank.orders.domain.OrderType): com.mvv.bank.orders.rest.entities.OrderType = enumMappingFunc[com.mvv.bank.orders.domain.OrderType, com.mvv.bank.orders.rest.entities.OrderType]()(source)
  //def orderTypeToDto(source: com.mvv.bank.orders.domain.OrderType): com.mvv.bank.orders.rest.entities.OrderType = enumMappingFunc[com.mvv.bank.orders.domain.OrderType, com.mvv.bank.orders.rest.entities.OrderType]()
  //def orderTypeToDomain(source: DtoOrderType): DomainOrderType = enumMappingFunc[DtoOrderType, DomainOrderType]()
  def OrderTypeToDto(source: DomainOrderType): DtoOrderType =
    enumMappingFunc[DomainOrderType, DtoOrderType]()(source)
  def OrderTypeToDomain(source: DtoOrderType): DomainOrderType =
    enumMappingFunc[DtoOrderType, DomainOrderType]()(source)

  def BuySellTypeToDto(source: DomainBuySellType): DtoBuySellType =
    enumMappingFunc[DomainBuySellType, DtoBuySellType]()(source)
  def BuySellTypeToDomain(source: DtoBuySellType): DomainBuySellType =
    enumMappingFunc[DtoBuySellType, DomainBuySellType]()(source)

  def DailyExecutionTypeToDto(source: DomainDailyExecutionType): DtoDailyExecutionType =
    enumMappingFunc[DomainDailyExecutionType, DtoDailyExecutionType]()(source)
  def DailyExecutionTypeDomain(source: DtoDailyExecutionType): DomainDailyExecutionType =
    enumMappingFunc[DtoDailyExecutionType, DomainDailyExecutionType]()(source)

  def SideToDto(source: DomainSide): DtoSide =
    enumMappingFunc[DomainSide, DtoSide]()(source)
  def SideToDomain(source: DtoSide): DomainSide =
    enumMappingFunc[DtoSide, DomainSide]()(source)

  def OrderStateToDto(source: DomainOrderState): DtoOrderState =
    enumMappingFunc[DomainOrderState, DtoOrderState]()(source)
  def OrderStateToDomain(source: DtoOrderState): DomainOrderState =
    enumMappingFunc[DtoOrderState, DomainOrderState]()(source)



