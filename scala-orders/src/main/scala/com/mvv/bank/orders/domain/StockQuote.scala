package com.mvv.bank.orders.domain


import scala.language.strictEquality
//
import java.time.{ LocalDate, LocalTime, ZonedDateTime }
import com.mvv.nullables.NullableCanEqualGivens


// A stock quote is the price of a stock as quoted on an exchange.
// A basic quote for a specific stock provides information, such as its bid and ask price,
// last traded price, and volume traded.
// https://www.investopedia.com/terms/s/stockquote.asp
// https://www.wallstreetmojo.com/stock-quote/
case class StockQuote (
  market: MarketSymbol,
  company: CompanySymbol, // symbol for this market; see https://www.investopedia.com/terms/s/stocksymbol.asp

  timestamp: ZonedDateTime,
  marketDate: LocalDate,
  marketTime: LocalTime,

  bid: Amount,
  ask: Amount,

  last: Option[Amount] = None,

  high: Option[Amount] = None,
  low: Option[Amount] = None,

  high52week: Option[Amount] = None,
  low52week: Option[Amount] = None,

  lastTradedPrice: Option[Amount] = None,
  lastTradedPriceDatetime: Option[Amount] = None,

  previousClose: Option[Amount] = None,
  open: Option[Amount] = None,
  close: Option[Amount] = None,

  change: Option[Amount] = None,
  changePercent: Option[BigDecimal] = None,

  volume: Option[BigDecimal] = None,
  //averageVolume: Option[BigDecimal] = None,
  volume3m: Option[BigDecimal] = None,
  ) extends Quote :
  override def product: String = company.value


object StockQuote extends NullableCanEqualGivens[StockQuote] :
  def apply(
    market: Market,
    company: Company,
    timestamp: ZonedDateTime,
    bid: BigDecimal,
    ask: BigDecimal,
    currency: Currency,
  ): StockQuote = StockQuote(
    market = market.symbol,
    company = company.symbol,
    timestamp = timestamp,
    marketDate = timestamp.withZoneSameInstant(market.zoneId).nn.toLocalDate.nn,
    marketTime = timestamp.withZoneSameInstant(market.zoneId).nn.toLocalTime.nn,
    bid = Amount.of(bid, currency),
    ask = Amount.of(ask, currency),
  )

  extension (quote: StockQuote)
    def asPrice(buySellType: BuySellType): Amount =
        buySellType match
          case BuySellType.BUY  => quote.ask
          case BuySellType.SELL => quote.bid

