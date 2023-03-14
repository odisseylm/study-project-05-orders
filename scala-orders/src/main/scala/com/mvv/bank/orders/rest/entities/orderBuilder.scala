package com.mvv.bank.orders.rest.entities

import java.time.ZonedDateTime
import scala.annotation.targetName


/**
 * It is designed for easy migration from kotlin project.
 */

//noinspection ScalaFileName
object OrderBuilder :

  implicit class MutableVal[T](val setter: T=>Unit)  {
    inline def apply(v: T): Unit = setter(v)
    @targetName("assign")
    inline infix def <=(v: T): Unit = setter(v)
  }

  def id(using o: BaseOrder): MutableVal[Option[Long]] = MutableVal(o.id = _)
  def user(using o: BaseOrder): MutableVal[String] = MutableVal(o.user = _)
  def market(using o: BaseOrder): MutableVal[String] = MutableVal(o.market = _)
  def orderType(using o: BaseOrder): MutableVal[OrderType] = MutableVal(o.orderType = _)
  def orderState(using o: BaseOrder): MutableVal[OrderState] = MutableVal(o.orderState = _)
  def side(using o: BaseOrder): MutableVal[Side] = MutableVal(o.side = _)
  def buySellType(using o: BaseOrder): MutableVal[BuySellType] = MutableVal(o.buySellType = _)
  def volume(using o: BaseOrder): MutableVal[BigDecimal] = MutableVal(o.volume = _)

  def limitPrice(using o: BaseOrder): MutableVal[Option[Amount]] = MutableVal(o.limitPrice = _)
  def stopPrice(using o: BaseOrder): MutableVal[Option[Amount]] = MutableVal(o.stopPrice = _)
  def dailyExecutionType(using o: BaseOrder): MutableVal[Option[DailyExecutionType]] = MutableVal(o.dailyExecutionType = _)

  def resultingPrice(using o: BaseOrder): MutableVal[Option[Amount]] = MutableVal(o.resultingPrice = _)

  def placedAt(using o: BaseOrder): MutableVal[Option[ZonedDateTime]] = MutableVal(o.placedAt = _)
  def executedAt(using o: BaseOrder): MutableVal[Option[ZonedDateTime]] = MutableVal(o.executedAt = _)
  def canceledAt(using o: BaseOrder): MutableVal[Option[ZonedDateTime]] = MutableVal(o.canceledAt = _)
  def expiredAt(using o: BaseOrder): MutableVal[Option[ZonedDateTime]] = MutableVal(o.expiredAt = _)



object CashOrderBuilder :

  import OrderBuilder.MutableVal

  extension (_order: CashOrder)
    def fillOrder(init: CashOrder ?=> Unit): CashOrder =
      given order: CashOrder = _order
      //noinspection ScalaUnusedExpression, it is really called 'by-name'
      init
      order

  object kotlinStyle:
    def CashOrder(): CashOrder = new CashOrder()

    extension (_order: BaseOrder)
      def apply(init: BaseOrder ?=> Unit): BaseOrder =
        given order: BaseOrder = _order
        //noinspection ScalaUnusedExpression, it is really called 'by-name'
        init
        order


  def buyCurrency(using o: CashOrder): MutableVal[String] = MutableVal(o.buyCurrency = _)
  def sellCurrency(using o: CashOrder): MutableVal[String] = MutableVal(o.sellCurrency = _)
  def priceCurrency(using o: CashOrder): MutableVal[String] = MutableVal(o.priceCurrency = _)
  def resultingRate(using o: CashOrder): MutableVal[Option[FxRate]] = MutableVal(o.resultingRate = _)
