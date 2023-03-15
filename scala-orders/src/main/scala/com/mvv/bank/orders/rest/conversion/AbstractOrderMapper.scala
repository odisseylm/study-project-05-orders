package com.mvv.bank.orders.rest.conversion

import scala.compiletime.uninitialized
import scala.reflect.ClassTag
import jakarta.inject.Inject
import org.mapstruct.{ Mapper, MappingTarget, AfterMapping }
//
import com.mvv.bank.orders.domain.{
  Market as DomainMarket,
  Company as DomainCompany,
  OrderType as DomainOrderType,
  //AbstractOrder as DomainBaseOrder,
  MarketSymbol, CompanySymbol,
  MarketProvider, CompanyProvider,

  BuySellType as DomainBuySellType,
}

import com.mvv.bank.orders.domain.{
  OrderType as DomainOrderType,
  Side as DomainSide,
  BuySellType as DomainBuySellType,
  DailyExecutionType as DomainDailyExecutionType,
  OrderState as DomainOrderState,
}
import com.mvv.bank.orders.rest.entities.{
  OrderType as DtoOrderType,
  Side as DtoSide,
  BuySellType as DtoBuySellType,
  DailyExecutionType as DtoDailyExecutionType,
  OrderState as DtoOrderState,
}



type DomainBaseOrder = com.mvv.bank.orders.domain.Order[?,?]

abstract class AbstractOrderMapper :

  //noinspection VarCouldBeVal // TODO: try to use constructor
  @Inject
  private var marketProvider: MarketProvider = uninitialized
  //def marketToDomain(marketSymbol: String): DomainMarket = marketToDomain(MarketSymbol(marketSymbol))
  def marketToDomain(marketSymbol: MarketSymbol): DomainMarket = marketProvider.marketBySymbol(marketSymbol)

  //@Inject
  //private var domainMappers: DomainMappers = uninitialized

  //noinspection VarCouldBeVal
  @Inject
  private var _enumMappers: EnumMappers = uninitialized
  protected def enumMappers: EnumMappers = _enumMappers

  //noinspection VarCouldBeVal
  //@Inject
  //private var companyProvider: CompanyProvider = uninitialized
  //def companyToDomain(companySymbol: String): DomainCompany = companyToDomain(CompanySymbol(companySymbol))
  //def companyToDomain(companySymbol: CompanySymbol): DomainCompany = companyProvider.companyBySymbol(companySymbol)

  //noinspection ScalaUnusedSymbol
  @AfterMapping
  def validateDomainOrderAfterCreation(source: Any, @MappingTarget target: DomainBaseOrder): Unit =
      target.validateCurrentState()
