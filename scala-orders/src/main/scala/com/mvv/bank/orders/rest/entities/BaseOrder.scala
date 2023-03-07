package com.mvv.bank.orders.rest.entities

import com.mvv.bank.orders.rest.*

import java.time.ZonedDateTime
import scala.compiletime.uninitialized


//sealed
class BaseOrder {
    //var id: Long = uninitialized
    var id: Option[Long] = None
    /*lateinit*/ var user: String = uninitialized
    /*lateinit*/ var market: String = uninitialized

    /*lateinit*/ var orderType: OrderType = uninitialized
    /*lateinit*/ var orderState: OrderState = uninitialized
    /*lateinit*/ var side: Side = uninitialized
    /*lateinit*/ var buySellType: BuySellType = uninitialized
    /*lateinit*/ var volume: BigDecimal = uninitialized

    var limitPrice: Option[Amount] = None
    var stopPrice:  Option[Amount] = None
    var dailyExecutionType: Option[DailyExecutionType] = None

    var placedAt:   Option[ZonedDateTime] = None
    var executedAt: Option[ZonedDateTime] = None
    var canceledAt: Option[ZonedDateTime] = None
    var expiredAt:  Option[ZonedDateTime] = None
}


// CashOrder

class CashOrder extends BaseOrder {
  /*lateinit*/ var buyCurrency: String = uninitialized
  /*lateinit*/ var sellCurrency: String = uninitialized
  var resultingRate: Option[FxRate] = None
}
