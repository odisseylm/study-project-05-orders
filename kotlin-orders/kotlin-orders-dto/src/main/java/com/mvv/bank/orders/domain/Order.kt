package com.mvv.bank.orders.domain

import com.mvv.bank.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.ZonedDateTime
import com.mvv.bank.orders.domain.Quote as BaseQuote


private val log: Logger = LoggerFactory.getLogger(Order::class.java)


sealed interface Order<Product: Any, Quote: BaseQuote> {
    var id: Long?
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
    var placedAt:   ZonedDateTime?
    var executedAt: ZonedDateTime?
    var canceledAt: ZonedDateTime?
    var expiredAt:  ZonedDateTime?

    var marketSymbol: String
    var market: Market

    var orderState: OrderState

    var resultingPrice: Amount?
    // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
    var resultingQuote: Quote?

    // you can change state only to proper next valid state
    fun changeOrderState(nextOrderState: OrderState, context: OrderContext)
    fun validateCurrentState()
    fun validateNextState(nextState: OrderState)

    fun toExecute(quote: Quote): Boolean
}

interface OrderNaturalKey { }


inline fun <reified T: Order<*,*>> createOrder(init: T.() -> Unit): T {
    val order = newInstance<T>()
    order.init()
    order.validateCurrentState()
    return order
}


sealed class AbstractOrder<Product: Any, Quote: BaseQuote> : Order<Product, Quote> {
    override var id: Long? = null

    override lateinit var user: User

    private val sideImpl = LateInitProperty<Side, Any>(
        changeable = false,
        changeErrorMessage = "Changing order side is not allowed (from \${prev} to \${new}).",
    )
    override var side: Side by sideImpl

    override lateinit var product: Product
    override lateinit var volume: BigDecimal

    override lateinit var market: Market
    override lateinit var marketSymbol: String

    override lateinit var buySellType: BuySellType
    override var orderState: OrderState = OrderState.UNKNOWN

    // probably it would be better to use Instant? But I do not see advantages of Instant comparing with ZonedDateTime
    override var placedAt:   ZonedDateTime? = null
    override var executedAt: ZonedDateTime? = null
    override var canceledAt: ZonedDateTime? = null
    override var expiredAt:  ZonedDateTime? = null

    override var resultingPrice: Amount? = null
    // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
    override var resultingQuote: Quote? = null

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

    override fun changeOrderState(nextOrderState: OrderState, context: OrderContext) {
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
        checkPropertyInitialized(::marketSymbol)
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
        checkInitialized(::marketSymbol)
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
}
