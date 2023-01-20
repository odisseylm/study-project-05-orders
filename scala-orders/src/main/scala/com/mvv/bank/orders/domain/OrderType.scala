package com.mvv.bank.orders.domain

import scala.reflect.ClassTag


// TODO: temp, remove after adding/implementing these classes
trait StockOrder
trait AbstractCashOrder

trait CashMarketOrder extends AbstractCashOrder
trait StockMarketOrder extends StockOrder
trait CashLimitOrder extends AbstractCashOrder
trait StockLimitOrder extends StockOrder
trait CashStopOrder extends AbstractCashOrder
trait StockStopOrder extends StockOrder


enum OrderType (
  val cashDomainType: Class[? <: AbstractCashOrder],
  val stockDomainType: Class[? <: StockOrder],
  ) {
  case MARKET_ORDER extends OrderType(classOf[CashMarketOrder], classOf[StockMarketOrder])
  case LIMIT_ORDER extends OrderType(classOf[CashLimitOrder], classOf[StockLimitOrder])
  case STOP_ORDER extends OrderType(classOf[CashStopOrder], classOf[StockStopOrder])
}


enum BuySellType extends Enum[BuySellType] {
  //case
  //  BUY,
  //  SELL // TODO: why we cannot use ',' at the end
  case
      BUY
    , SELL
}


enum DailyExecutionType (val humanName: String) :
  case DAY_ONLY extends DailyExecutionType("Day Only")
  case GTC      extends DailyExecutionType("Good 'til Canceled")



/**
 * I am not 100% sure that is the same as Buy-Side/Sell-Side, but seems so :-)
 * https://corporatefinanceinstitute.com/resources/career/buy-side-vs-sell-side/
 */
enum Side :
  case
    /**
     * Trade/orders for clients.
     *
     * Buy-Side according to https://corporatefinanceinstitute.com/resources/career/buy-side-vs-sell-side/
     * Buy-Side – is the side of the financial market that buys and invests large portions of securities for the purpose of money or fund management.
     * The Buy Side refers to firms that purchase securities and includes investment managers, pension funds, and hedge funds.
     */
     CLIENT

    /**
     * Bank or market.
     * Sell-Side according to https://corporatefinanceinstitute.com/resources/career/buy-side-vs-sell-side/
     * Sell-Side – is the other side of the financial market, which deals with the creation, promotion,
     * and selling of traded securities to the public.
     * The Sell-Side refers to firms that issue, sell, or trade securities, and includes investment banks,
     * advisory firms, and corporations.
     */
    , BANK_MARKET // bank or market



enum OrderState :
  case
      UNKNOWN
    , TO_BE_PLACED
    , PLACED
    , EXECUTED
    , EXPIRED
    , CANCELED



/*
// optional API (can be removed)
val OrderState.asVerb get() =
    when (this) {
        OrderState.UNKNOWN  -> "make unknown"
        OrderState.PLACED   -> "place"
        OrderState.EXECUTED -> "execute"
        OrderState.CANCELED -> "cancel"
        OrderState.EXPIRED  -> "expire"
    }
*/

/*
enum class OrderCancelReason {
    EXPIRED,
    CANCELED_BY_USER,
}
*/


