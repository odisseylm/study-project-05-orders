package com.mvv.bank.orders.domain.test.predefined

import scala.language.unsafeNulls
//
import java.time.{ Clock, DayOfWeek, Instant, LocalDate, LocalTime, ZoneId }
//
import com.mvv.nullables.DayOfWeekCanEqualGivens
import com.mvv.bank.orders.domain.*


object TestPredefinedUsers :
  val USER1: User = User("vovan@gmail.com") //, "vovan")
  //val USER2: User = User("petro@gmail.com") //, "petro")


object TestPredefinedMarkets extends MarketProvider :
  val NASDAQ: Market = TestMarketImpl("National Association of Securities Dealers Automated Quotation",
      MarketSymbol.of("NASDAQ"), ZoneId.of("America/New_York"), "",
      LocalTime.of(9, 0), LocalTime.of(17, 0))
  val KYIV1:  Market = TestMarketImpl("KYIV1", MarketSymbol.of("KYIV1"),
      ZoneId.of("Europe/Kiev"), "", LocalTime.of(9, 0), LocalTime.of(17, 0))

  override def marketBySymbol(marketSymbol: MarketSymbol): Market = marketSymbol match
    case NASDAQ.symbol => NASDAQ
    case KYIV1.symbol  => KYIV1
    case _ => throw IllegalArgumentException("Market [$marketSymbol] is not found.")


object TestPredefinedCompanies extends CompanyProvider :
  val APPLE: Company = TestCompanyImpl("Apple", CompanySymbol.of("AAPL"), "isin1234")

  override def companyBySymbol(companySymbol: CompanySymbol): Company = companySymbol match
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
    import DayOfWeekCanEqualGivens.given
    (date.getDayOfWeek == DayOfWeek.SATURDAY) || (date.getDayOfWeek == DayOfWeek.SUNDAY)


class TestDateTimeService (
  override val zoneId: ZoneId = ZoneId.systemDefault(),
  override val clock: Clock = Clock.systemDefaultZone(),
  private val nowSupplier: ()=> Instant = () => Instant.now()
  ) extends DateTimeService :
  override def now(): Instant = nowSupplier()
