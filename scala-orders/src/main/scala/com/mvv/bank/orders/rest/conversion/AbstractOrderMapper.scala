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
  /*
  It causes errors:
  error: Can't generate mapping method with no input arguments.
  error: Can't generate mapping method with return type void.

  // with no input arguments.
  name = {SharedNameTable$NameImpl@5375} "marketProvider"
  type = {Type$MethodType@5376} "()com.mvv.bank.orders.domain.MarketProvider"
  owner = {Symbol$ClassSymbol@5377} "com.mvv.bank.orders.rest.conversion.AbstractOrderMapper"

  // with return type void
  name = {SharedNameTable$NameImpl@5402} "marketProvider_$eq"
  type = {Type$MethodType@5403} "(com.mvv.bank.orders.domain.MarketProvider)void"
  owner = {Symbol$ClassSymbol@5377} "com.mvv.bank.orders.rest.conversion.AbstractOrderMapper"

  name = {SharedNameTable$NameImpl@5415} "companyProvider"
  type = {Type$MethodType@5416} "()com.mvv.bank.orders.domain.CompanyProvider"
  owner = {Symbol$ClassSymbol@5377} "com.mvv.bank.orders.rest.conversion.AbstractOrderMapper"

  name = {SharedNameTable$NameImpl@5429} "companyProvider_$eq"
  type = {Type$MethodType@5430} "(com.mvv.bank.orders.domain.CompanyProvider)void"
  owner = {Symbol$ClassSymbol@5377} "com.mvv.bank.orders.rest.conversion.AbstractOrderMapper"


  */
  // TODO: try to make it working with 'protected'
  @Inject
  private /*lateinit*/ var marketProvider: MarketProvider = uninitialized
  @Inject
  private /*lateinit*/ var companyProvider: CompanyProvider = uninitialized
//  @Inject
//  protected /*lateinit*/ var marketProvider: MarketProvider
//  @Inject
//  protected /*lateinit*/ var companyProvider: CompanyProvider

  // Ideally it should be put into separate MarketMapper but easy pure unit testing it is there now
  // (to avoid injection sub-dependencies into dependencies)
  def marketToDto(market: DomainMarket): String = market.symbol.value
  def marketToDomain(marketSymbol: String): DomainMarket =
    marketToDomain(MarketSymbol(marketSymbol))
  def marketToDomain(marketSymbol: MarketSymbol): DomainMarket =
    marketProvider.marketBySymbol(marketSymbol)

  // Ideally it should be put into separate MarketMapper but easy pure unit testing it is there now
  // (to avoid injection sub-dependencies into dependencies)
  def companyToDto(company: DomainCompany): String = company.symbol.value
  def companyToDomain(companySymbol: String): DomainCompany =
    companyToDomain(CompanySymbol(companySymbol))
  def companyToDomain(companySymbol: CompanySymbol): DomainCompany =
    companyProvider.companyBySymbol(companySymbol)

  //@Suppress("UNCHECKED_CAST")
  //protected def <T> newOrderInstance(type: KClass<*>): T = internalNewInstance(type) as T
  //protected def newOrderInstance[T](_type: ClassTag[T]): T = ??? //internalNewInstance(_type).asInstanceOf[T]

  @AfterMapping
  /*open*/ def validateDomainOrderAfterCreation(source: Any, @MappingTarget target: DomainBaseOrder): Unit =
      target.validateCurrentState()
  
  // It is designed to choose cash or stock order.
  //abstract def chooseOrderTypeClass(orderType: DomainOrderType): KClass<*>
  /*abstract*/ //def chooseOrderTypeClass(orderType: DomainOrderType): ClassTag[?]

  //def temp: String = "gfg"



