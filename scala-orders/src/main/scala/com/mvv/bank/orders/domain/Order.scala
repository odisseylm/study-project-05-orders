package com.mvv.bank.orders.domain

import org.slf4j.Logger

import java.time.ZonedDateTime
import scala.reflect.ClassTag
import com.mvv.utils.newInstance
import com.mvv.scala.props.KProperty
import com.mvv.scala.props.LateInitProperty

import scala.compiletime.uninitialized



private val log: Logger = ??? // LoggerFactory.getLogger(classOf[Order[AnyRef,AnyRef]])

trait BaseQuote // TODO: temp, remove after adding/implementing BaseQuote

//sealed
trait Order[Product <: AnyRef, Quote <: BaseQuote] {
  var tempLong666: Long
  var tempString666: String
  var id: Long|Null
  var user: User
  var side: Side
  val orderType: OrderType
  var buySellType: BuySellType
  var product: Product
  // for most equities it will be integer (but for currencies and for some equities it will be float numbers)
  var volume: BigDecimal

  // Several variables are used to see problems in case of signal race abd if both
  // operations are happened 'cancel' and 'execute/expire'.
  // Probably it would be better to use Instant? But I do not see advantages of Instant comparing with ZonedDateTime.
  var placedAt:   ZonedDateTime|Null
  var executedAt: ZonedDateTime|Null
  var canceledAt: ZonedDateTime|Null
  var expiredAt:  ZonedDateTime|Null

  var market: Market

  var orderState: OrderState

  var resultingPrice: Amount|Null
  // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
  var resultingQuote: Quote|Null

  // you can change state only to proper next valid state
  def changeOrderState(nextOrderState: OrderState, context: OrderContext): Unit
  def validateCurrentState(): Unit
  def validateNextState(nextState: OrderState): Unit

  def toExecute(quote: Quote): Boolean
}


trait OrderNaturalKey
  // T O D O: implement

type BaseOrder = Order[_,_]

//inline def createOrder[T: ClassTag[_ <: Order333]](init: () => Unit): T =
inline def createOrder[T <: BaseOrder](init: BaseOrder => Unit)(implicit ct: ClassTag[T]): T =
  val order = newInstance[T]()
  init(order)
  order.validateCurrentState()
  order




//sealed
abstract class AbstractOrder[Product <: AnyRef, Quote <: BaseQuote] extends Order[Product, Quote] {

  // This class mainly is introduced to avoid 'duplicate code' warning
  //@Suppress("ClassName") // It is named from '_' (as internal) because I cannot do it protected
  /*protected*/ trait _BaseAttrs[Product <: AnyRef, Quote <: BaseQuote] {
    val id: Long|Null
    val user: User
    val side: Side
    val buySellType: BuySellType
    val volume: BigDecimal

    val market: Market

    val orderState: OrderState

    val placedAt:   ZonedDateTime|Null
    val executedAt: ZonedDateTime|Null
    val canceledAt: ZonedDateTime|Null
    val expiredAt:  ZonedDateTime|Null

    val resultingPrice: Amount|Null
    val resultingQuote: Quote|Null

    protected def copyToOrder(order: AbstractOrder[Product, Quote]): Unit = {
      order.id = id
      order.user = user

      order.side  = side
      order.buySellType  = buySellType
      order.volume = volume

      order.market = market

      order.orderState = orderState

      order.placedAt   = placedAt
      order.executedAt = executedAt
      order.canceledAt = canceledAt
      order.expiredAt  = expiredAt

      order.resultingPrice = resultingPrice
      order.resultingQuote = resultingQuote
    }
  }

  //override var tempLong666: Long = uninitialized
  var tempLong666: Long = 666
  var tempString666: String = uninitialized

  var id: Long|Null //= uninitialized

  /*lateinit*/ var user: User //= uninitialized

  private val _side = LateInitProperty[Side, AnyRef](
    changeable = false,
    changeErrorMessage = "Changing order side is not allowed (from ${prev} to ${new}).", // !!! ordinal string !!!
  )
  //override var _side: Side by sideImpl   // Scala does not support property delegation
  def side: Side = _side.getValue(this, KProperty.simpleProperty[Side]("side"))
  def side_= (side: Side): Unit = _side.set(side)

  /*lateinit*/ var product: Product
  /*lateinit*/ var volume: BigDecimal

  /*lateinit*/ var market: Market

  /*lateinit*/ var buySellType: BuySellType
  var orderState: OrderState = OrderState.UNKNOWN

  // probably it would be better to use Instant? But I do not see advantages of Instant comparing with ZonedDateTime
  var placedAt:   ZonedDateTime|Null = null
  var executedAt: ZonedDateTime|Null = null
  var canceledAt: ZonedDateTime|Null = null
  var expiredAt:  ZonedDateTime|Null = null

  var resultingPrice: Amount|Null = null
  // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
  var resultingQuote: Quote|Null = null

  /*
  private var orderStateValue: OrderState = orderState // there 'orderState' is param
  // Seems there is no easier way to implement it using provided by kotlin field/value for setter
  // because this property is inherited, and kotlin does not allow to create private setter.
  override val orderState: OrderState get() = orderStateValue

  // Seems there is no easier way ...
  private var placedAtValue: ZonedDateTime? = placedAt // there 'placedAt' is param
  override val placedAt: ZonedDateTime? get() = placedAtValue

  // Seems there is no easier way ...
  private var marketValue: Market? = market // there 'market' is param
  override val market: Market? get() = marketValue
  */

  /*
  override fun changeOrderState(nextOrderState: OrderState, context: OrderContext): Unit = {
    val currentOrderState = this.orderState
    if (nextOrderState == currentOrderState) {
      log.warn("Attempt to set the same state for order $id.") // T O D O: probably not needed
      return
    }

    validateCurrentState()
    validateNextState(nextOrderState)

    this.orderState = nextOrderState

    when (orderState) {
      OrderState.UNKNOWN -> throw IllegalStateException("Impossible to change state to $orderState.")
      OrderState.TO_BE_PLACED -> {
      }
      OrderState.PLACED -> {
        // it is used if it is done locally (instead of real server)
        this.placedAt = context.now()
        this.market = context.market
      }
      OrderState.EXECUTED -> {
        // it is used if it is done locally there (instead of real server)
        this.executedAt = context.now()
      }
      OrderState.CANCELED -> {
        // it is used if it is done locally there (instead of real server)
        this.canceledAt = context.now()
      }
      OrderState.EXPIRED -> {
        // it is used if it is done locally there (instead of real server)
        this.expiredAt = context.now()
      }
    }
  }

  override fun validateCurrentState() {
    if (orderState == OrderState.UNKNOWN) {
      log.warn("Attempt to validate current state of order with status $orderState.")
      return
    }

    checkLateInitPropsAreInitialized(this)
    check(side == Side.CLIENT) { "Currently only client side orders are supported." }

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

    when (orderState) {
      OrderState.UNKNOWN -> { }
      OrderState.TO_BE_PLACED -> {
        check(id == null)
      }
      OrderState.PLACED -> {
        checkId(id)
      }
      OrderState.EXECUTED -> {
        checkId(id)
        checkPropertyInitialized(::placedAt)
        checkPropertyInitialized(::resultingPrice)
        checkPropertyInitialized(::resultingQuote)
      }
      OrderState.EXPIRED -> {
        checkId(id)
        checkPropertyInitialized(::expiredAt)
      }
      OrderState.CANCELED -> {
        checkId(id)
        checkPropertyInitialized(::canceledAt)
      }
    }
  }

  override fun validateNextState(nextState: OrderState) {
    if (nextState == OrderState.UNKNOWN) {
      return
    }

    checkLateInitPropsAreInitialized(this)

    /*
    checkInitialized(::orderType)
    checkInitialized(::product)
    checkInitialized(::volume)
    checkInitialized(::market)
    checkInitialized(::buySellType)
    checkInitialized(::orderState)
    */

    @Suppress("KotlinConstantConditions")
    when (nextState) {
      OrderState.UNKNOWN -> { }
      OrderState.TO_BE_PLACED -> {
        check(id == null)
        check(this.orderState == OrderState.UNKNOWN) {
          "Impossible to to place order with status ${this.orderState}." }
      }
      OrderState.PLACED -> {
        log.warn("Placing/booking order is done on server side and nothing to validate.")
        //checkId(id)
      }
      OrderState.EXPIRED -> {
        checkId(id)
        check(this.orderState == OrderState.PLACED) {
          "Impossible to to expire order with status ${this.orderState}." }
      }
      OrderState.CANCELED -> {
        checkId(id)
        check(this.orderState == OrderState.PLACED) {
          "Impossible to to cancel order with status ${this.orderState}." }
      }
      OrderState.EXECUTED -> {
        checkId(id)
        check(this.orderState == OrderState.PLACED) {
          "Impossible to to cancel order with status ${this.orderState}." }
      }
    }
  }
  */
}
