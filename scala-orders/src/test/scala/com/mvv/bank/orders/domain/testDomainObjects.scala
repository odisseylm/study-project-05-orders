package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId


object TestPredefinedUsers :
  val USER1: User = User.of("vovan@gmail.com") //, "vovan")
  //val USER2 = User.of("petro@gmail.com") //, "petro")



object TestPredefinedMarkets extends MarketProvider :
  val NASDAQ: Market = TestMarketImpl("National Association of Securities Dealers Automated Quotation",
      MarketSymbol("NASDAQ"), ZoneId.of("America/New_York").nn, "",
      LocalTime.of(9, 0).nn, LocalTime.of(17, 0).nn)
  val KYIV1:  Market = TestMarketImpl("KYIV1", MarketSymbol("KYIV1"),
      ZoneId.of("Europe/Kiev").nn, "", LocalTime.of(9, 0).nn, LocalTime.of(17, 0).nn)

  override def marketBySymbol(marketSymbol: MarketSymbol): Market =
    marketSymbol match
      case NASDAQ.symbol => NASDAQ
      case KYIV1.symbol  => KYIV1
      case _ => throw IllegalArgumentException("Market [$marketSymbol] is not found.")


object TestPredefinedCompanies extends CompanyProvider :
  val APPLE: Company = TestCompanyImpl("Apple", CompanySymbol("AAPL"), "isin1234")

  override def companyBySymbol(companySymbol: CompanySymbol): Company =
    companySymbol match
      case _ if companySymbol == APPLE.symbol => APPLE
      case _ => throw IllegalArgumentException("Company [$companySymbol] is not found.")


private class TestCompanyImpl (
  override val name: String,
  override val symbol: CompanySymbol,
  override val isin: String,
  ) extends Company


private class TestMarketImpl (
  override val name: String,
  override val symbol: MarketSymbol,
  override val zoneId: ZoneId,
  override val description: String,
  override val defaultOpenTime: LocalTime,  // inclusive
  override val defaultCloseTime: LocalTime, // exclusive
  ) extends Market :
  override def isWorkingDay(date: LocalDate): Boolean =
    import com.mvv.nullables.DayOfWeekCanEqualGivens.given
    (date.getDayOfWeek == DayOfWeek.SATURDAY) || (date.getDayOfWeek == DayOfWeek.SUNDAY)
