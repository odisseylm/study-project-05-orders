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
import com.mvv.utils.{ uncapitalize, isUninitializedId }
import com.mvv.log.{ safe, Logger, LoggerMixin }
import com.mvv.nullables.{ isNull, isNotNull, NullableCanEqualGivens }
import com.mvv.props.{ checkPropInitialized, checkNamedValueInitialized }

import org.mvv.scala.tools.props.{ namedValue, PropertyValue, fromPropertyValue, lateInitProp }
import org.mvv.scala.tools.props.{ currentClassIsInitializedPropsBy, DefaultIsInitializedMethods }
import org.mvv.scala.tools.quotes.{ classNameOf, simpleTypeNameOf }
import com.mvv.scala.props.checkRequiredPropsAreInitialized



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

object Order // extends NullableCanEqualGivens[Order] // T O D O: why compile error

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

  import scala.language.implicitConversions

  private val _id = lateInitProp[Option[Long]]
  def id: Option[Long] = _id
  def id_=(id: Option[Long]): Unit = _id(id)

  private val _user = lateInitProp[User] // uninitialized
  def user: User = _user
  def user_=(user: User): Unit = _user(user)

  private val _side = lateInitProp[Side]
  def side: Side = _side.value
  def side_= (side: Side): Unit = _side.value = side

  private val _product = lateInitProp[Product] // uninitialized
  def product: Product = _product
  protected def product_=(product: Product): Unit = _product(product)

  private val _volume = lateInitProp[BigDecimal] // uninitialized
  def volume: BigDecimal = _volume
  def volume_=(volume: BigDecimal): Unit = _volume(volume)

  private val _market = lateInitProp[Market] // uninitialized
  def market: Market = _market
  def market_=(market: Market): Unit = _market(market)

  private val _buySellType= lateInitProp[BuySellType] // uninitialized
  def buySellType: BuySellType = _buySellType
  def buySellType_=(buySellType: BuySellType): Unit = _buySellType(buySellType)

  protected var _orderState: OrderState = OrderState.UNKNOWN
  def orderState: OrderState = _orderState
  def orderState_=(orderState: OrderState): Unit = _orderState = orderState

  // probably it would be better to use Instant? But I do not see advantages of Instant comparing with ZonedDateTime
  private val _placedAt = lateInitProp[Option[ZonedDateTime]]
  def placedAt: Option[ZonedDateTime] = _placedAt
  def placedAt_=(placedAt: Option[ZonedDateTime]): Unit = _placedAt(placedAt)

  private val _executedAt = lateInitProp[Option[ZonedDateTime]]
  def executedAt: Option[ZonedDateTime] = _executedAt
  def executedAt_=(executedAt: Option[ZonedDateTime]): Unit = _executedAt(executedAt)

  private val _canceledAt = lateInitProp[Option[ZonedDateTime]]
  def canceledAt: Option[ZonedDateTime] = _canceledAt
  def canceledAt_=(canceledAt: Option[ZonedDateTime]): Unit = _canceledAt(canceledAt)

  private val _expiredAt = lateInitProp[Option[ZonedDateTime]]
  def expiredAt:  Option[ZonedDateTime] = _expiredAt
  def expiredAt_=(expiredAt: Option[ZonedDateTime]): Unit = _expiredAt(expiredAt)

  private val _resultingPrice = lateInitProp[Option[Amount]]
  def resultingPrice: Option[Amount] = _resultingPrice
  def resultingPrice_=(resultingPrice: Option[Amount]): Unit = _resultingPrice(resultingPrice)

  // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
  private val _resultingQuote = lateInitProp[Option[Quote]]
  def resultingQuote: Option[Quote] = _resultingQuote
  protected def resultingQuote_= (quote: Option[Quote]): Unit = _resultingQuote(quote)

  protected def currentIsInitProps: List[(String, ()=>Boolean)] =
    currentClassIsInitializedPropsBy[OrderIsInitializedMethods]


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
        this.placedAt = Some(context.now())
        this.market = context.market
      case OrderState.EXECUTED =>
        // it is used if it is done locally there (instead of real server)
        this.executedAt = context.now()
      case OrderState.CANCELED =>
        // it is used if it is done locally there (instead of real server)
        this.canceledAt = context.now()
      case OrderState.EXPIRED =>
        // it is used if it is done locally there (instead of real server)
        this.expiredAt = context.now()
    }
  }

  override def validateCurrentState(): Unit = {
    if (orderState == OrderState.UNKNOWN) {
      //log.warn(s"Attempt to validate current state of order with status ${orderState.safe}.")
      return
    }

    checkRequiredPropsAreInitialized(simpleTypeNameOf(this), currentIsInitProps)
    check(side == Side.CLIENT, s"Currently only client side orders are supported.")

    orderState match {
      case OrderState.UNKNOWN => // nothing to do
      case OrderState.TO_BE_PLACED =>
        import com.mvv.nullables.AnyCanEqualGivens.given
        check(id == null)
      case OrderState.PLACED =>
        checkId(id)
      case OrderState.EXECUTED =>
        checkId(id)
        checkNamedValueInitialized(placedAt)
        // or
        //checkPropInitialized(_placedAt)
        checkPropInitialized(_resultingPrice)
        checkPropInitialized(_resultingQuote)
      case OrderState.EXPIRED =>
        checkId(id)
        checkPropInitialized(_expiredAt)
      case OrderState.CANCELED =>
        checkId(id)
        checkPropInitialized(_canceledAt)
    }
  }

  override def validateNextState(nextState: OrderState): Unit = {
    if (nextState == OrderState.UNKNOWN) { return }

    checkRequiredPropsAreInitialized(simpleTypeNameOf(this), currentIsInitProps)

    nextState match {
      case OrderState.UNKNOWN => // nothing to do
      case OrderState.TO_BE_PLACED =>
        check(isUninitializedId(id), s"Seems order $id is already placed.")
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
      order.id = id
      order.user = user

      order.side = side
      order.buySellType = buySellType
      order.volume = volume

      order.market = market

      order.orderState = orderState

      order.placedAt = placedAt
      order.executedAt = executedAt
      order.canceledAt = canceledAt
      order.expiredAt = expiredAt

      order.resultingPrice = resultingPrice
      order.resultingQuote = resultingQuote
    end copyToOrder
    val resultingPrice: Option[Amount]

    val resultingQuote: Option[Q]
  end _BaseAttrs


class OrderIsInitializedMethods

object OrderIsInitializedMethods extends DefaultIsInitializedMethods
