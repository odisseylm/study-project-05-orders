package com.mvv.bank.orders.rest.entities

import com.mvv.bank.orders.rest.*

import java.time.ZonedDateTime
import scala.compiletime.uninitialized


//sealed
class BaseOrder {
    //var id: Long = uninitialized
    var id: Option[Long] = None
    var user: String = uninitialized
    var market: String = uninitialized

    var orderType: OrderType = uninitialized
    var orderState: OrderState = uninitialized
    var side: Side = uninitialized
    var buySellType: BuySellType = uninitialized
    var volume: BigDecimal = uninitialized

    var limitPrice: Option[Amount] = None
    var stopPrice:  Option[Amount] = None
    var dailyExecutionType: Option[DailyExecutionType] = None

    var resultingPrice:  Option[Amount] = None

    var placedAt:   Option[ZonedDateTime] = None
    var executedAt: Option[ZonedDateTime] = None
    var canceledAt: Option[ZonedDateTime] = None
    var expiredAt:  Option[ZonedDateTime] = None
}


// CashOrder

class CashOrder extends BaseOrder {
  var buyCurrency:   String = uninitialized
  var sellCurrency:  String = uninitialized
  var priceCurrency: String = uninitialized
  var resultingRate: Option[FxRate] = None
}
