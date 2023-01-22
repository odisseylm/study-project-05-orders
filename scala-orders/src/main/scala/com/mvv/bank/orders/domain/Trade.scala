package com.mvv.bank.orders.domain

import scala.language.strictEquality
//
import java.time.ZonedDateTime
//
import com.mvv.log.safe
import com.mvv.utils.check
import com.mvv.nullables.NullableCanEqualGivens


trait Trade[P] :
  def market: MarketSymbol
  def product: P
  def buySellType: BuySellType
  def volume: BigDecimal // integer in case of stocks; float in case of cash/money
  def price: Amount
  def amount: Amount = volume * price
  def tradedAt: ZonedDateTime


case class FxCashTrade (
  id: Long,

  override val market: MarketSymbol,

  override val buySellType: BuySellType,
  buyCurrency: Currency,
  sellCurrency: Currency,
  override val volume: BigDecimal,
  override val price: Amount,

  override val tradedAt: ZonedDateTime,
  ) extends Trade[Currency] :

  check(price.currency == priceCurrency,
    s"Seems price ${price.safe} has wrong currency for deal $this.")

  //@Deprecated("Better to use buyCurrency/sellCurrency directly if you access/config cash trade by direct CashTrade typed variable.")
  override def product: Currency = buySellType match
      //null => null
    case BuySellType.BUY  => buyCurrency
    case BuySellType.SELL => sellCurrency

  def priceCurrency: Currency = buySellType match
      // opposite
      //null -> null
    case BuySellType.BUY  => sellCurrency
    case BuySellType.SELL => buyCurrency

object FxCashTrade extends NullableCanEqualGivens[FxCashTrade]


case class StockTrade (
  id: Long,

  override val market: MarketSymbol,
  company: CompanySymbol,

  override val buySellType: BuySellType,
  override val volume: BigDecimal,
  override val price: Amount,
  override val tradedAt: ZonedDateTime,
  ) extends Trade[CompanySymbol] derives CanEqual :
  override def product: CompanySymbol = company


object StockTrade extends NullableCanEqualGivens[StockTrade]
