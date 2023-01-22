//noinspection ScalaUnusedSymbol
package com.mvv.bank.orders.domain


import scala.language.strictEquality
//
import scala.annotation.targetName
//
import java.math.RoundingMode
import java.time.{ LocalDate, LocalTime, ZonedDateTime }
//
import com.mvv.utils.check
import com.mvv.bank.orders.domain.CurrencyPair
import com.mvv.nullables.NullableCanEqualGivens


// In Foreign Exchange:
//  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
//  ask - price of client 'buy'  (and dealer/bank 'sell')
//
// TODO: how to deal with inverted rates???
// TODO: 'bid < ask' rule should be applied for which counterCurrency???
//
case class FxRate (
  market: MarketSymbol,
  timestamp: ZonedDateTime,
  marketDate: LocalDate,
  marketTime: LocalTime,

  // The CurrencyPair as in Ccy1/Ccy2 where the Bid will be the price for selling ccy1 and buying ccy2.
  currencyPair: CurrencyPair,

  // This FxRate might come from a combination of 2 rates through a cross currency, if it is the case,
  // return the cross currency.
  // Optional<String> crossCcy

  // we do not use there ask, bid, mid
  bid: BigDecimal,
  ask: BigDecimal,

  // true if the CurrencyPair follows market convention, e.g. EUR/USD and not USD/EUR (but it gets more complicated for
  // other cross-currencies, e.g. CHF/JPY?)
  //boolean isMarketConvention();
  ) :
  def mid: BigDecimal = (bid + ask) / BigDecimal.valueOf(2) // math context is not needed there (at least now)
  override def toString: String = s"$currencyPair $mid($bid/$ask)"

object FxRate extends NullableCanEqualGivens[FxRate] :
  def apply(market: Market, timestamp: ZonedDateTime, currencyPair: CurrencyPair, bid: BigDecimal, ask: BigDecimal): FxRate =
    FxRate(
      market = market.symbol, timestamp = timestamp,
      marketDate = timestamp.withZoneSameInstant(market.zoneId).nn.toLocalDate.nn,
      marketTime = timestamp.withZoneSameInstant(market.zoneId).nn.toLocalTime.nn,
      currencyPair = currencyPair, bid = bid, ask = ask
    )

  extension (rate: FxRate)
    def spread: BigDecimal = rate.ask - rate.bid
    // TODO: should we swap bid and ask ???
    def inverted: FxRate = rate.copy(currencyPair = rate.currencyPair.inverted, bid = invertRate(rate.bid), ask = invertRate(rate.ask))
    def containsCurrency(currency: Currency): Boolean = rate.currencyPair.containsCurrency(currency)

    def asPrice(priceCurrency: Currency, buySellType: BuySellType): Amount =
      check(rate.containsCurrency(priceCurrency),
        "FX rate ${this.currencyPair} does not contain currency $priceCurrency.")

      val price: BigDecimal = buySellType match
        case BuySellType.BUY  => rate.bid
        case BuySellType.SELL => rate.ask

      val fixedPriceValue: BigDecimal = if (rate.currencyPair.counter == priceCurrency) price else invertRate(price)
      Amount(fixedPriceValue, priceCurrency)


case class FxRateAsQuote (
  rate: FxRate,
  priceCurrency: Currency,
  ) extends Quote derives CanEqual :

  check(priceCurrency == rate.currencyPair.base || priceCurrency == rate.currencyPair.counter,
    s"FX rate ${rate.currencyPair} does not suite order currencies (with price currency $priceCurrency).")

  override def product: String = rate.currencyPair.oppositeCurrency(priceCurrency).toString()
  override def market: MarketSymbol = rate.market
  override def marketDate: LocalDate = rate.marketDate
  override def marketTime: LocalTime = rate.marketTime
  override def timestamp: ZonedDateTime = rate.timestamp
  override def bid: Amount = Amount(
    // TODO: should be invertRate(rate.bid) or invertRate(rate.ask)
    if (priceCurrency == rate.currencyPair.counter) rate.bid else invertRate(rate.bid), priceCurrency)
  override val ask: Amount = Amount(
    // TODO: should be invertRate(rate.ask) or invertRate(rate.bid)
    if (priceCurrency == rate.currencyPair.counter) rate.ask else invertRate(rate.ask), priceCurrency)


def invertRate(price: BigDecimal): BigDecimal =
  import com.mvv.nullables.AnyCanEqualGivens
  if (price == BigDecimal(0))
    // or throw exception? In general, it is unexpected value...
    return BigDecimal(0)

  val precision = price.precision
  // T O D O: write more logical solution
  val resScale = if (price.scale < 2) precision + 2 + (2 - price.scale)
                 else precision + price.scale

  BigDecimal( java.math.BigDecimal.ONE.nn
    .divide(price.underlying(), resScale, RoundingMode.HALF_UP).nn
    .stripTrailingZeros().nn )

