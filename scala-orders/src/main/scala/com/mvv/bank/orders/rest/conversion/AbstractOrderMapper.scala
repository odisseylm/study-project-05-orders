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
  // We do not use there protected var because scala generates setter method for it
  // and MapStruct fails trying to use this setter as mapping function
  @Inject
  private var _marketProvider: MarketProvider = uninitialized
  //noinspection ScalaWeakerAccess
  protected def marketProvider: MarketProvider = _marketProvider

  //noinspection VarCouldBeVal
  //@Inject
  //private var _companyProvider: CompanyProvider = uninitialized
  //noinspection ScalaWeakerAccess,ScalaUnusedSymbol
  //protected def companyProvider: CompanyProvider = _companyProvider

  //noinspection VarCouldBeVal
  @Inject
  private var _enumMappers: EnumMappers = uninitialized
  protected def enumMappers: EnumMappers = _enumMappers

  // Ideally it should be put into separate MarketMapper but to make easy unit testing it is put there now
  // (to avoid injection sub-dependencies into dependencies)
  def marketToDto(market: DomainMarket): String = market.symbol.value
  def marketToDomain(marketSymbol: String): DomainMarket =
    marketToDomain(MarketSymbol(marketSymbol))
  def marketToDomain(marketSymbol: MarketSymbol): DomainMarket =
    marketProvider.marketBySymbol(marketSymbol)

  // Ideally it should be put into separate MarketMapper but easy pure unit testing it is there now
  // (to avoid injection sub-dependencies into dependencies)
  //def companyToDto(company: DomainCompany): String = company.symbol.value
  //def companyToDomain(companySymbol: String): DomainCompany =
  //  companyToDomain(CompanySymbol(companySymbol))
  //def companyToDomain(companySymbol: CompanySymbol): DomainCompany =
  //  companyProvider.companyBySymbol(companySymbol)

  //noinspection ScalaUnusedSymbol
  @AfterMapping
  def validateDomainOrderAfterCreation(source: Any, @MappingTarget target: DomainBaseOrder): Unit =
      target.validateCurrentState()
