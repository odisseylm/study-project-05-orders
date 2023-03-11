package com.mvv.bank.orders.rest.conversion

import org.mapstruct.{BeanMapping, Mapper, Mapping}
import com.mvv.bank.orders.domain.FxRate as DomainFxRate
import com.mvv.bank.orders.domain.{CurrencyPair as DomainCurrencyPair, MarketSymbol as DomainMarketSymbol}
import com.mvv.bank.orders.rest.entities.FxRate as DtoFxRate


// TODO: Unmapped target property: "currencyPair". Occured at 'FxRate fxRateToDomain(FxRate source)' in 'FxRateMapper'
// TODO: Unmapped target properties: "cur1, cur2". Occured at 'FxRate fxRateToDto(FxRate source)' in 'FxRateMapper'


//@Mapper
trait FxRateMapper :
  //@Mapping(target = "currencyPair", expression = "CurrencyPair.of(source.cur1(), source.cur2()) ")
  //def fxRateToDomain(source: DtoFxRate): DomainFxRate
  def fxRateToDomain(source: DtoFxRate): DomainFxRate =
    DomainFxRate(
      market = DomainMarketSymbol(source.market),
      timestamp = source.timestamp,
      marketDate = source.marketDate,
      marketTime = source.marketTime,
      currencyPair = DomainCurrencyPair(source.cur1, source.cur2),
      bid = source.bid,
      ask = source.ask,
    )
  //def fxRateCurrencyPair(source: DtoFxRate): DomainCurrencyPair =
  //  DomainCurrencyPair(source.cur1, source.cur2)
  def fxRateToDomain(source: Option[DtoFxRate]): Option[DomainFxRate] = source.map(fxRateToDomain)

  @Mapping(target = "cur1", source = "currencyPair.base")
  @Mapping(target = "cur2", source = "currencyPair.counter")
  //@Mapping(target = "mid", ignore = true)
  @BeanMapping(ignoreUnmappedSourceProperties = Array("mid"))
  def fxRateToDto(source: DomainFxRate): DtoFxRate
  def fxRateToDto(source: Option[DomainFxRate]): Option[DtoFxRate] =
    source.map(fxRateToDto)
