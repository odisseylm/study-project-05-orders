//noinspection ScalaUnusedSymbol // T O D O: remove after adding test and so on
package com.mvv.bank.orders.domain


import scala.language.strictEquality
//
import scala.reflect.ClassTag
import scala.compiletime.uninitialized
//
import java.time.ZonedDateTime
//
import com.mvv.utils.{ newInstance, check, checkId, checkNotNull, checkNotBlank, require, requireNotBlank }
import com.mvv.utils.{ removePrefix, uncapitalize}
import com.mvv.log.{safe, Logger, LoggerMixin}
import com.mvv.nullables.{ isNull, NullableCanEqualGivens }
import com.mvv.scala.props.{ checkRequiredPropsAreInitialized, checkPropertyInitialized, BeanProp, KProperty, LateInitProperty }



type BaseQuote = Quote

//sealed
trait Order[Product, Quote <: BaseQuote] :
  def id: Option[Long]

  def user: User
  def market: Market
  def side: Side

  def orderType: OrderType
  def buySellType: BuySellType
  def product: Product
  // for most equities it will be integer (but for currencies and for some equities it will be float numbers)
  def volume: BigDecimal

  // Several variables are used to see problems in case of signal race abd if both
  // operations are happened 'cancel' and 'execute/expire'.
  // Probably it would be better to use Instant? But I do not see advantages of Instant comparing with ZonedDateTime.
  def placedAt:   Option[ZonedDateTime]
  def executedAt: Option[ZonedDateTime]
  def canceledAt: Option[ZonedDateTime]
  def expiredAt:  Option[ZonedDateTime]

  def orderState: OrderState

  def resultingPrice: Option[Amount]
  // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
  def resultingQuote: Option[Quote]

  // you can change state only to proper next valid state
  def changeOrderState(nextOrderState: OrderState)(using context: OrderContext): Unit
  def validateCurrentState(): Unit
  def validateNextState(nextState: OrderState): Unit

  def toExecute(quote: Quote): Boolean
end Order

object Order // extends NullableCanEqualGivens[Order] // TODO: why compile error

trait OrderNaturalKey
  // T O D O: implement

type BaseOrder = Order[?,?]


inline def createOrder[T <: BaseOrder](init: T => Unit)(implicit ct: ClassTag[T]): T =
  val order = newInstance[T]()
  init(order)
  order.validateCurrentState()
  order


//sealed
abstract class AbstractOrder[Product <: AnyRef, Quote <: BaseQuote] extends Order[Product, Quote] with LoggerMixin {

  // This class mainly is introduced to avoid 'duplicate code' warning
  //@Suppress("ClassName") // It is named from '_' (as internal) because I cannot do it protected

  protected var _id: Option[Long] = None
  def id: Option[Long] = _id

  protected var _user: User = uninitialized
  def user: User = _user

  private val _side = LateInitProperty[Side, AnyRef](
    changeable = false,
    changeErrorMessage = "Changing order side is not allowed (from ${prev} to ${new}).", // !!! ordinal string !!!
  )
  def side: Side = _side.getValue(this, KProperty.simpleProperty[Side]("side"))
  protected def side_= (side: Side): Unit = _side.set(side)

  protected var _product: Product = uninitialized
  def product: Product = _product

  protected var _volume: BigDecimal = uninitialized
  def volume: BigDecimal = _volume

  protected var _market: Market = uninitialized
  def market: Market = _market

  protected var _buySellType: BuySellType = uninitialized
  def buySellType: BuySellType = _buySellType

  protected var _orderState: OrderState = OrderState.UNKNOWN
  def orderState: OrderState = _orderState

  // probably it would be better to use Instant? But I do not see advantages of Instant comparing with ZonedDateTime
  protected var _placedAt:   Option[ZonedDateTime] = None
  def placedAt: Option[ZonedDateTime] = _placedAt

  protected var _executedAt: Option[ZonedDateTime] = None
  def executedAt: Option[ZonedDateTime] = _executedAt

  protected var _canceledAt: Option[ZonedDateTime] = None
  def canceledAt: Option[ZonedDateTime] = _canceledAt

  protected var _expiredAt:  Option[ZonedDateTime] = None
  def expiredAt:  Option[ZonedDateTime] = _expiredAt

  protected var _resultingPrice: Option[Amount] = None
  def resultingPrice: Option[Amount] = _resultingPrice

  // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
  private var _resultingQuote: Option[Quote] = None
  def resultingQuote: Option[Quote] = _resultingQuote
  protected def resultingQuote_= (quote: Option[Quote]): Unit = _resultingQuote = quote


  override def changeOrderState(nextOrderState: OrderState)(using context: OrderContext): Unit = {
    val currentOrderState = this.orderState
    if (nextOrderState == currentOrderState) {
      log.warn(s"Attempt to set the same state for order ${id.safe}.") // T O D O: probably not needed
      return
    }

    validateCurrentState()
    validateNextState(nextOrderState)

    this._orderState = nextOrderState
    import com.mvv.implicits.ImplicitConversion.autoOption

    orderState match {
      case OrderState.UNKNOWN => throw IllegalStateException(s"Impossible to change state to ${orderState.safe}.")
      case OrderState.TO_BE_PLACED => // Nothing to do
      case OrderState.PLACED =>
        // it is used if it is done locally (instead of real server)
        this._placedAt = Some(context.now())
        this._market = context.market
      case OrderState.EXECUTED =>
        // it is used if it is done locally there (instead of real server)
        this._executedAt = context.now()
      case OrderState.CANCELED =>
        // it is used if it is done locally there (instead of real server)
        this._canceledAt = context.now()
      case OrderState.EXPIRED =>
        // it is used if it is done locally there (instead of real server)
        this._expiredAt = context.now()
    }
  }

  override def validateCurrentState(): Unit = {
    if (orderState == OrderState.UNKNOWN) {
      //log.warn(s"Attempt to validate current state of order with status ${orderState.safe}.")
      return
    }

    checkRequiredPropsAreInitialized(this)
    check(side == Side.CLIENT, s"Currently only client side orders are supported.")

    /*
    checkPropertyInitialized(::orderType)
    checkPropertyInitialized(::side)
    check(side == Side.CLIENT) { "Currently only client side orders are supported." }
    checkPropertyInitialized(::volume)
    checkPropertyInitialized(::buySellType)
    checkPropertyInitialized(::orderState)

    checkPropertyInitialized(::product)
    checkPropertyInitialized(::market)
    */

    orderState match {
      case OrderState.UNKNOWN => // nothing to do
      case OrderState.TO_BE_PLACED =>
        import com.mvv.nullables.AnyCanEqualGivens.given
        check(id == null)
      case OrderState.PLACED =>
        checkId(id)
      case OrderState.EXECUTED =>
        checkId(id)
        checkPropertyInitialized(BeanProp(placedAt))
        checkPropertyInitialized(BeanProp(resultingPrice))
        checkPropertyInitialized(BeanProp(resultingQuote))
      case OrderState.EXPIRED =>
        checkId(id)
        checkPropertyInitialized(BeanProp(expiredAt))
      case OrderState.CANCELED =>
        checkId(id)
        checkPropertyInitialized(BeanProp(canceledAt))
    }
  }

  override def validateNextState(nextState: OrderState): Unit = {
    if (nextState == OrderState.UNKNOWN) { return }

    checkRequiredPropsAreInitialized(this)

    /*
    checkInitialized(::orderType)
    checkInitialized(::product)
    checkInitialized(::volume)
    checkInitialized(::market)
    checkInitialized(::buySellType)
    checkInitialized(::orderState)
    */

    nextState match {
      case OrderState.UNKNOWN => // nothing to do
      case OrderState.TO_BE_PLACED =>
        check(id.isNull, s"Seems order $id is already placed.")
        check(this.orderState == OrderState.UNKNOWN,
          s"Impossible to to place order with status ${this.orderState.safe}.")
      case OrderState.PLACED =>
        log.warn(s"Placing/booking order is done on server side and nothing to validate.")
        //checkId(id)
      case OrderState.EXPIRED =>
        checkId(id)
        check(this.orderState == OrderState.PLACED,
          s"Impossible to to expire order with status ${this.orderState.safe}.")
      case OrderState.CANCELED =>
        checkId(id)
        check(this.orderState == OrderState.PLACED,
          s"Impossible to to cancel order with status ${this.orderState.safe}.")
      case OrderState.EXECUTED =>
        checkId(id)
        check(this.orderState == OrderState.PLACED,
          s"Impossible to to execute order with status ${this.orderState.safe}.")
    }
  }
}


object AbstractOrder :
  //protected
  trait _BaseAttrs[P <: AnyRef, Q <: BaseQuote, OrderType <: AbstractOrder[P, Q]] : // TODO: try to remain only Order type parameter
    val id: Option[Long]
    val user: User
    val side: Side
    val buySellType: BuySellType
    val volume: BigDecimal

    val market: Market

    val orderState: OrderState

    val placedAt: Option[ZonedDateTime]
    val executedAt: Option[ZonedDateTime]
    val canceledAt: Option[ZonedDateTime]
    val expiredAt: Option[ZonedDateTime]

    protected def copyToOrder(order: OrderType): Unit =
      order._id = id
      order._user = user

      order._side(side)
      order._buySellType = buySellType
      order._volume = volume

      order._market = market

      order._orderState = orderState

      order._placedAt = placedAt
      order._executedAt = executedAt
      order._canceledAt = canceledAt
      order._expiredAt = expiredAt

      order._resultingPrice = resultingPrice
      order._resultingQuote = resultingQuote
    end copyToOrder
    val resultingPrice: Option[Amount]

    val resultingQuote: Option[Q]
  end _BaseAttrs

