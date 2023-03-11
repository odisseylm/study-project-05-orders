package com.mvv.bank.orders.domain


import scala.language.strictEquality
//
import scala.compiletime.uninitialized
//
import java.time.ZonedDateTime
//
import com.mvv.log.safe
import com.mvv.utils.{ requireNotNull, check }
import com.mvv.nullables.NullableCanEqualGivens
import com.mvv.props.checkPropInitialized
import org.mvv.scala.tools.props.{ namedValue, currentClassIsInitializedProps, lateInitProp }
import org.mvv.scala.tools.props.currentClassIsInitializedPropsBy


sealed abstract class AbstractCashOrder extends AbstractOrder[Currency, Quote] {
  import org.mvv.scala.tools.props.fromPropertyValue

  private val _buyCurrency = lateInitProp[Currency] // uninitialized
  def buyCurrency: Currency = _buyCurrency
  def buyCurrency_=(buyCurrency: Currency): Unit = _buyCurrency(buyCurrency)


  private val _sellCurrency = lateInitProp[Currency] // uninitialized
  def sellCurrency: Currency = _sellCurrency
  def sellCurrency_=(sellCurrency: Currency): Unit = _sellCurrency(sellCurrency)


  // It also can be used to set resultingPrice/resultingQuote from FX rate during order execution (if they are not set).
  // It is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost).
  private val _resultingRate = lateInitProp[Option[FxRate]]
  def resultingRate: Option[FxRate] = _resultingRate
  // TODO: make it protected
  def resultingRate_= (value: Option[FxRate]): Unit = {
    requireNotNull(value)

    _resultingRate(value)
    import com.mvv.nullables.AnyCanEqualGivens.given

    if value.nonEmpty then
      if resultingPrice.isEmpty then
        resultingPrice = value.map(_.asPrice(priceCurrency, buySellType)) // TODO: test with inverted rate
      if resultingQuote.isEmpty then
        resultingQuote = value.map(FxRateAsQuote(_, priceCurrency)) // TODO: test with inverted rate
  }

  def priceCurrency: Currency = buySellType match
    // opposite
    case BuySellType.BUY  => sellCurrency
    case BuySellType.SELL => buyCurrency

  @Deprecated("Better to use buyCurrency/sellCurrency directly if you access/config cash order by direct FxCashOrder typed variable.")
  override def product: Currency = buySellType match
    case BuySellType.BUY  => buyCurrency
    case BuySellType.SELL => sellCurrency

  override def product_= (value: Currency): Unit =
     val buySellType = this.buySellType
     checkPropInitialized(namedValue(buySellType), s"buySellType should be set before setting product.")
     buySellType match
       case BuySellType.BUY  => _buyCurrency(value)
       case BuySellType.SELL => _sellCurrency(value)

  override protected def currentIsInitProps: List[(String, () => Boolean)] =
    currentClassIsInitializedPropsBy[OrderIsInitializedMethods] ++ super.currentIsInitProps


  def toExecute(rate: FxRate): Boolean = toExecute(FxRateAsQuote(rate, priceCurrency))
}


object AbstractCashOrder :
  class Base (
    override val id: Option[Long] = None,
    override val user: User,
    override val side: Side,
    override val buySellType: BuySellType,
    val buyCurrency: Currency,
    val sellCurrency: Currency,
    override val volume: BigDecimal,

    override val market: Market,

    override val orderState: OrderState = OrderState.UNKNOWN,

    override val placedAt: Option[ZonedDateTime] = None,
    override val executedAt: Option[ZonedDateTime] = None,
    override val canceledAt: Option[ZonedDateTime] = None,
    override val expiredAt: Option[ZonedDateTime] = None,

    val resultingRate: Option[FxRate] = None,
    override val resultingPrice: Option[Amount] = None,
    override val resultingQuote: Option[Quote] = None,

  ) extends AbstractOrder._BaseAttrs[Currency, Quote, AbstractCashOrder]:

    override def copyToOrder(order: AbstractCashOrder): Unit =
      super.copyToOrder(order)
      order.buyCurrency = buyCurrency
      order.sellCurrency = sellCurrency
      order.resultingRate = resultingRate
  end Base


class CashLimitOrder private () extends AbstractCashOrder, LimitOrder[Currency, Quote] :
  import org.mvv.scala.tools.props.fromPropertyValue

  private val limitOrderSupport = StopLimitOrderSupport[Currency, Quote](this,
      //::limitPrice,
      //::dailyExecutionType,
      "limitPrice", () => limitPrice, // TODO: use read-only props
      "dailyExecutionType,", () => dailyExecutionType,
  )

  override val orderType: OrderType = OrderType.LIMIT_ORDER

  private val _limitPrice = lateInitProp[Amount] // uninitialized
  override def limitPrice: Amount = _limitPrice
  def limitPrice_=(limitPrice: Amount): Unit = _limitPrice(limitPrice)

  private val _dailyExecutionType = lateInitProp[DailyExecutionType] // uninitialized
  override def dailyExecutionType: DailyExecutionType = _dailyExecutionType
  def dailyExecutionType_=(dailyExecutionType: DailyExecutionType): Unit = _dailyExecutionType(dailyExecutionType)

  override protected def currentIsInitProps: List[(String, () => Boolean)] =
    currentClassIsInitializedPropsBy[OrderIsInitializedMethods] ++ super.currentIsInitProps

  override def validateCurrentState(): Unit =
    super.validateCurrentState()
    limitOrderSupport.validateCurrentState()

    check(limitPrice.currency == priceCurrency,
      s"Limit price currency (${limitPrice.currency.safe}) differs from price currency (${priceCurrency.safe}).")

  override def validateNextState(nextState: OrderState): Unit =
        super.validateNextState(nextState)
        limitOrderSupport.validateNextState(nextState)

  override def toExecute(quote: Quote): Boolean = limitOrderSupport.toExecute(quote)

end CashLimitOrder


object CashLimitOrder extends NullableCanEqualGivens[CashLimitOrder]:
  def uninitialized(): CashLimitOrder = new CashLimitOrder()

  //noinspection DuplicatedCode
  def apply(
          base: AbstractCashOrder.Base,
          limitPrice: Amount,
          dailyExecutionType: DailyExecutionType,
  ): CashLimitOrder =
    val order = new CashLimitOrder()
    base.copyToOrder(order)

    order.limitPrice = limitPrice
    order.dailyExecutionType = dailyExecutionType

    order.validateCurrentState()
    order



class CashStopOrder private () extends AbstractCashOrder, StopOrder[Currency, Quote] :
  import org.mvv.scala.tools.props.fromPropertyValue

  private val stopOrderSupport = StopLimitOrderSupport[Currency, Quote](
    this, "stoPrice", () => stopPrice, "dailyExecutionType", () => dailyExecutionType)
  // TODO: use it ::stopPrice, ::dailyExecutionType)

  override val orderType: OrderType = OrderType.STOP_ORDER

  private val _stopPrice = lateInitProp[Amount] // uninitialized
  override def stopPrice: Amount = _stopPrice
  def stopPrice_=(stopPrice: Amount): Unit = _stopPrice(stopPrice)

  private val _dailyExecutionType = lateInitProp[DailyExecutionType] // uninitialized
  override def dailyExecutionType: DailyExecutionType = _dailyExecutionType
  def dailyExecutionType_=(dailyExecutionType: DailyExecutionType): Unit = _dailyExecutionType(dailyExecutionType)

  override protected def currentIsInitProps: List[(String, () => Boolean)] =
    currentClassIsInitializedPropsBy[OrderIsInitializedMethods] ++ super.currentIsInitProps

  override def validateCurrentState(): Unit =
    super.validateCurrentState()
    stopOrderSupport.validateCurrentState()

    check(stopPrice.currency == priceCurrency,
      "Stop price currency (${stopPrice.currency.safe}) differs from price currency (${priceCurrency.safe}).")

  override def validateNextState(nextState: OrderState): Unit =
    super.validateNextState(nextState)
    stopOrderSupport.validateNextState(nextState)

  override def toExecute(quote: Quote): Boolean = stopOrderSupport.toExecute(quote)

end CashStopOrder

object CashStopOrder extends NullableCanEqualGivens[CashStopOrder] :
  def uninitialized(): CashStopOrder = new CashStopOrder()
  //noinspection DuplicatedCode
  def apply(
      base: AbstractCashOrder.Base,
      stopPrice: Amount,
      dailyExecutionType: DailyExecutionType,
  ): CashStopOrder =
      val order = new CashStopOrder()
      base.copyToOrder(order)

      order.stopPrice = stopPrice
      order.dailyExecutionType = dailyExecutionType

      order.validateCurrentState()
      order

end CashStopOrder



class CashMarketOrder private () extends AbstractCashOrder :
  override val orderType: OrderType = OrderType.MARKET_ORDER

  override protected def currentIsInitProps: List[(String, () => Boolean)] =
    currentClassIsInitializedPropsBy[OrderIsInitializedMethods] ++ super.currentIsInitProps

  override def toExecute(quote: Quote): Boolean =
    val rateCurrencyPair = CurrencyPair(Currency (quote.product), quote.bid.currency)
    val orderCurrencyPair = CurrencyPair(buyCurrency, sellCurrency)

    check (rateCurrencyPair == orderCurrencyPair || rateCurrencyPair == orderCurrencyPair.inverted,
    s"FX rate currencies ${rateCurrencyPair.safe} does not suite order currencies ${orderCurrencyPair.safe}.")
    true


object CashMarketOrder extends NullableCanEqualGivens[CashMarketOrder] :
  def uninitialized(): CashMarketOrder = new CashMarketOrder()
  def apply(base: AbstractCashOrder.Base): CashMarketOrder =
    val order = new CashMarketOrder()
    base.copyToOrder(order)
    order.validateCurrentState()
    order
