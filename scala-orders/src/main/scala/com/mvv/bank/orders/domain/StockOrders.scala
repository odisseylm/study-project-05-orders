package com.mvv.bank.orders.domain

import org.mvv.scala.tools.props.PropType

import scala.language.strictEquality
//
import scala.compiletime.uninitialized
//
import java.time.ZonedDateTime
//
import org.mvv.scala.tools.props.{ PropertyValue, readOnlyProp }
import com.mvv.nullables.NullableCanEqualGivens
import com.mvv.utils.check


sealed abstract class AbstractStockOrder extends AbstractOrder[CompanySymbol, StockQuote] {

  private val _company = PropertyValue[Company](
    PropType.LateInit,
    "company",
    changeable = false,
    postUpdate = { (newV, _) => product = newV.symbol },
  )
  def company: Company = _company.value

  //private var _resultingQuote: Option[StockQuote] = None
  //override def resultingQuote: Option[StockQuote] = _resultingQuote
  //override def resultingQuote: Option[Quote] = _resultingQuote
  override def resultingQuote_= (quote: Option[StockQuote]): Unit =
    super.resultingQuote_= (quote)
    if quote.isDefined && resultingPrice.isEmpty then
      resultingPrice = quote.map(_.asPrice(buySellType))
}


object AbstractStockOrder extends NullableCanEqualGivens[AbstractStockOrder] :
  class Base (
    override val id: Option[Long] = None,
    override val user: User,
    override val side: Side,
    override val buySellType: BuySellType,
    val company: Company,
    override val volume: BigDecimal,

    override val market: Market,

    override val orderState: OrderState = OrderState.UNKNOWN,

    override val placedAt: Option[ZonedDateTime] = None,
    override val executedAt: Option[ZonedDateTime] = None,
    override val canceledAt: Option[ZonedDateTime] = None,
    override val expiredAt: Option[ZonedDateTime] = None,

    override val resultingPrice: Option[Amount] = None,
    override val resultingQuote: Option[StockQuote] = None,
  ) extends AbstractOrder._BaseAttrs[CompanySymbol, StockQuote, AbstractStockOrder] :
    override def copyToOrder(order: AbstractStockOrder): Unit =
      super.copyToOrder(order)
      order._company.value = company


class StockLimitOrder extends AbstractStockOrder, LimitOrder[CompanySymbol, StockQuote] {

  override val orderType: OrderType = OrderType.LIMIT_ORDER

  private var _limitPrice: Amount = uninitialized
  override def limitPrice: Amount = _limitPrice

  private var _dailyExecutionType: DailyExecutionType = uninitialized
  override def dailyExecutionType: DailyExecutionType = _dailyExecutionType

  private val limitOrderSupport = StopLimitOrderSupport(this,
    readOnlyProp(limitPrice),
    readOnlyProp(dailyExecutionType),
  )

  override def validateCurrentState(): Unit =
    super.validateCurrentState()
    limitOrderSupport.validateCurrentState()

  override def validateNextState (nextState: OrderState): Unit =
    super.validateNextState(nextState)
    limitOrderSupport.validateNextState(nextState)

  override def toExecute(quote: StockQuote): Boolean = limitOrderSupport.toExecute(quote)
}

object StockLimitOrder extends NullableCanEqualGivens[StockLimitOrder] :
  //noinspection DuplicatedCode
  def apply(
      base: AbstractStockOrder.Base,
      limitPrice: Amount,
      dailyExecutionType: DailyExecutionType,
  ): StockLimitOrder =
    val order = new StockLimitOrder()
    base.copyToOrder(order)

    order._limitPrice = limitPrice
    order._dailyExecutionType = dailyExecutionType

    order.validateCurrentState()
    order


class StockStopOrder extends AbstractStockOrder, StopOrder[CompanySymbol, StockQuote] :

  override val orderType: OrderType = OrderType.STOP_ORDER

  private var _stopPrice: Amount = uninitialized
  override def stopPrice: Amount = _stopPrice

  private var _dailyExecutionType: DailyExecutionType = uninitialized
  override def dailyExecutionType: DailyExecutionType = _dailyExecutionType

  private val stopOrderSupport = StopLimitOrderSupport(this,
    readOnlyProp(stopPrice),
    readOnlyProp(dailyExecutionType),
  )

  override def validateCurrentState(): Unit =
    super.validateCurrentState()
    stopOrderSupport.validateCurrentState()

  override def validateNextState (nextState: OrderState): Unit =
    super.validateNextState(nextState)
    stopOrderSupport.validateNextState(nextState)

  override def toExecute (quote: StockQuote): Boolean = stopOrderSupport.toExecute(quote)

end StockStopOrder


object StockStopOrder extends NullableCanEqualGivens[StockStopOrder] :
  //noinspection DuplicatedCode
  def apply(
    base: AbstractStockOrder.Base,
    stopPrice: Amount,
    dailyExecutionType: DailyExecutionType,
  ): StockStopOrder =
    val order = new StockStopOrder()
    base.copyToOrder(order)

    order._stopPrice = stopPrice
    order._dailyExecutionType = dailyExecutionType

    order.validateCurrentState()
    order


class StockMarketOrder extends AbstractStockOrder :
  override val orderType: OrderType = OrderType.MARKET_ORDER

  override def toExecute(quote: StockQuote): Boolean =
    check(quote.product == this.product.value,
      s"This quote is for another product (order: $product, quote: ${quote.product}).")
    true


object StockMarketOrder extends NullableCanEqualGivens[StockMarketOrder] :
  def create(
    base: AbstractStockOrder.Base,
  ): StockMarketOrder =
    val order = StockMarketOrder()
    base.copyToOrder(order)

    order.validateCurrentState()
    order
