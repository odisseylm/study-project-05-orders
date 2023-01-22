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
import com.mvv.scala.props.{ checkPropertyInitialized, BeanProp }


sealed abstract class AbstractCashOrder extends AbstractOrder[Currency, Quote] {

  private var _buyCurrency: Currency = uninitialized
  def buyCurrency: Currency = _buyCurrency

  protected var _sellCurrency: Currency = uninitialized
  def sellCurrency: Currency = _sellCurrency

  // It also can be used to set resultingPrice/resultingQuote from FX rate during order execution (if they are not set).
  // It is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost).
  protected var _resultingRate: Option[FxRate] = None
  def resultingRate: Option[FxRate] = _resultingRate
  // TODO: make it protected
  def resultingRate_= (value: Option[FxRate]): Unit = {
    requireNotNull(value)

    _resultingRate = value
    import com.mvv.nullables.AnyCanEqualGivens.given

    if (value.isDefined)
      if resultingPrice.isEmpty then
        resultingPrice = value.map(_.asPrice(priceCurrency, buySellType)) // TODO: test with inverted rate
      if resultingQuote.isEmpty then
        resultingQuote = value.map(FxRateAsQuote(_, priceCurrency)) // TODO: test with inverted rate
  }

  def priceCurrency: Currency = buySellType match
    // opposite
    //null -> null
    case BuySellType.BUY  => sellCurrency
    case BuySellType.SELL => buyCurrency

  @Deprecated("Better to use buyCurrency/sellCurrency directly if you access/config cash order by direct FxCashOrder typed variable.")
  override def product: Currency = buySellType match
    //null -> null
    case BuySellType.BUY  => buyCurrency
    case BuySellType.SELL => sellCurrency

  def product_= (value: Currency): Unit =
     val buySellType = this.buySellType
     checkPropertyInitialized(BeanProp(buySellType), s"buySellType should be set before setting product.")
     buySellType match
       case BuySellType.BUY  => _buyCurrency  = value
       case BuySellType.SELL => _sellCurrency = value

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

  ) extends AbstractOrder._BaseAttrs[Currency, Quote]:

    def copyToOrder(order: AbstractCashOrder): Unit =
      super.copyToOrder(order)
      order._buyCurrency = buyCurrency
      order._sellCurrency = sellCurrency
      order.resultingRate = resultingRate
  end Base


trait CashMarketOrder extends AbstractCashOrder
trait CashStopOrder extends AbstractCashOrder



class CashLimitOrder private () extends AbstractCashOrder, LimitOrder[Currency, Quote] :

  private val limitOrderSupport = StopLimitOrderSupport[Currency, Quote](this,
      //::limitPrice,
      //::dailyExecutionType,
      "limitPrice", () => limitPrice,
      "dailyExecutionType,", () => dailyExecutionType,
  )

  override val orderType: OrderType = OrderType.LIMIT_ORDER

  private var _limitPrice: Amount = uninitialized
  override def limitPrice: Amount = _limitPrice

  private var _dailyExecutionType: DailyExecutionType = uninitialized
  override def dailyExecutionType: DailyExecutionType = _dailyExecutionType

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


object CashLimitOrder  extends NullableCanEqualGivens[CashLimitOrder]:
  def apply(
          base: AbstractCashOrder.Base,
          limitPrice: Amount,
          dailyExecutionType: DailyExecutionType,
  ): CashLimitOrder =
    val order = new CashLimitOrder()
    base.copyToOrder(order)

    order._limitPrice = limitPrice
    order._dailyExecutionType = dailyExecutionType

    order.validateCurrentState()
    order


/*
class CashStopOrder private constructor() : AbstractCashOrder(), StopOrder<Currency, Quote> {

    private val stopOrderSupport = StopLimitOrderSupport(this, ::stopPrice, ::dailyExecutionType)

    override val orderType: OrderType = OrderType.STOP_ORDER
    override lateinit var stopPrice: Amount
    override lateinit var dailyExecutionType: DailyExecutionType

    override fun validateCurrentState() {
        super.validateCurrentState()
        stopOrderSupport.validateCurrentState()

        check(stopPrice.currency == priceCurrency) {
            "Stop price currency (${stopPrice.currency.safe}) differs from price currency (${priceCurrency.safe})." }
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)
        stopOrderSupport.validateNextState(nextState)
    }

    override fun toExecute(quote: Quote): Boolean = stopOrderSupport.toExecute(quote)

    companion object {
        fun create(
            base: Base,
            stopPrice: Amount,
            dailyExecutionType: DailyExecutionType,
        ): CashStopOrder {
            val order = CashStopOrder()
            base.copyToOrder(order)

            order.stopPrice   = stopPrice
            order.dailyExecutionType = dailyExecutionType

            order.validateCurrentState()
            return order
        }
    }
}


class CashMarketOrder private constructor() : AbstractCashOrder() {
    override val orderType: OrderType = OrderType.MARKET_ORDER

    override fun toExecute(quote: Quote): Boolean {
        val rateCurrencyPair = CurrencyPair.of(Currency.of(quote.product), quote.bid.currency)
        val orderCurrencyPair = CurrencyPair.of(buyCurrency, sellCurrency)

        check(rateCurrencyPair == orderCurrencyPair || rateCurrencyPair == orderCurrencyPair.inverted()) {
            "FX rate currencies $rateCurrencyPair does not suite order currencies $orderCurrencyPair." }
        return true
    }

    companion object {
        fun create(
            base: Base,
        ): CashMarketOrder {
            val order = CashMarketOrder()
            base.copyToOrder(order)
            order.validateCurrentState()
            return order
        }
    }
}
*/
