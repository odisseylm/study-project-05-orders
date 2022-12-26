package com.mvv.bank.orders.domain

import com.mvv.bank.shared.log.safe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import com.mvv.bank.orders.domain.Quote as BaseQuote


private val log: Logger = LoggerFactory.getLogger(Order::class.java)


sealed interface Order<Product: Any, Quote: BaseQuote> {
    var id: Long?
    var side: Side
    val orderType: OrderType
    var product: Product

    // several variables are used to see problems in case of signal race abd if both
    // operations are happened 'cancel' and 'execute/expire'
    var placedAt: Instant?
    var executedAt: Instant?
    var canceledAt: Instant?
    var expiredAt: Instant?

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

// This class is designed because kotlin does not support 'late init' props with custom getter/setter
class LateInitProperty<T, Owner> (
    value: T? = null,

    val changeable: Boolean = true,
    // !!! Message should have exactly ${prev} and ${new} (not short forms like $prev and $new)
    val changeErrorMessage: String = "Not allowed to change property (from \${prev} to \${new})",

    val validate:   (new: T, prev: T?)->Unit = {_,_->},
    val preUpdate:  (new: T, prev: T?)->Unit = {_,_->},
    val postUpdate: (new: T, prev: T?)->Unit = {_,_->},
) : ReadWriteProperty<Owner, T> {
    private var internalValue: T? = value
    val asNullableValue: T? get() = internalValue
    val asNonNullableValue: T get() = internalValue!!
    fun set(v: T) {
        val prev = this.internalValue
        validateNonChangeable(v, prev)
        validate(v, prev)
        preUpdate(v, prev)
        internalValue = v
        postUpdate(v, prev)
    }

    private fun validateNonChangeable(new: T, prev: T?) {
        if (prev != null && new != prev) {
            val msg = changeErrorMessage
                .replace("\$prev", prev.safe.toString())
                .replace("\$new", new.safe.toString())
            throw IllegalStateException(msg)
        }
    }

    // TODO: add logic to verify value on null only if T is nullable. Is it needed???
    override operator fun getValue(thisRef: Owner, property: KProperty<*>): T = asNonNullableValue
    override operator fun setValue(thisRef: Owner, property: KProperty<*>, value: T) = set(value)

    override fun toString(): String = "$internalValue"
    override fun equals(other: Any?): Boolean {
        return ((other is LateInitProperty<*, *>) && other.internalValue == this.internalValue)
                || other == internalValue
    }

    override fun hashCode(): Int = internalValue.hashCode()
}

sealed class AbstractOrder<Product: Any, Quote: BaseQuote> : Order<Product, Quote> {
    override var id: Long? = null

    private val sideImpl = LateInitProperty<Side, Any>(
        changeable = false,
        changeErrorMessage = "Changing order side is not allowed (from \${prev} to \${new}).",
    )
    override var side: Side by sideImpl

    override lateinit var product: Product
    override lateinit var market: Market

    lateinit var buySellType: BuySellType
    override var orderState: OrderState = OrderState.UNKNOWN

    override var placedAt: Instant? = null
    override var executedAt: Instant? = null
    override var canceledAt: Instant? = null
    override var expiredAt: Instant? = null

    override var resultingPrice: Amount? = null
    // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
    override var resultingQuote: Quote? = null

    /*
    private var orderStateValue: OrderState = orderState // there 'orderState' is param
    // Seems there is no easier way to implement it using provided by kotlin field/value for setter
    // because this property is inherited, and kotlin does not allow to create private setter.
    override val orderState: OrderState get() = orderStateValue

    // Seems there is no easier way ...
    private var placedAtValue: Instant? = placedAt // there 'placedAt' is param
    override val placedAt: Instant? get() = placedAtValue

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
            log.warn("Attempt to validate current state of order with status $orderState") // TODO: fix warning message
            return
        }

        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(orderType)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(side)
        check(side == Side.CLIENT) { "Currently only client side orders are supported." }
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(product)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(market)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(buySellType)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(orderState)

        when (orderState) {
            OrderState.UNKNOWN -> { }
            OrderState.TO_BE_PLACED -> {
                check(id == null)
            }
            OrderState.PLACED -> {
                checkNotNull(id)
            }
            OrderState.EXECUTED -> {
                checkNotNull(id)
                checkNotNull(placedAt)
                checkNotNull(resultingPrice)
                checkNotNull(resultingQuote)
            }
            OrderState.EXPIRED -> {
                checkNotNull(id)
                checkNotNull(expiredAt)
            }
            OrderState.CANCELED -> {
                checkNotNull(id)
                checkNotNull(canceledAt)
            }
        }
    }

    override fun validateNextState(nextState: OrderState) {
        if (nextState == OrderState.UNKNOWN) {
            return
        }

        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(orderType)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(product)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(market)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(buySellType)
        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(orderState)

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
                //checkNotNull(id)
            }
            OrderState.EXPIRED -> {
                checkNotNull(id)
                check(this.orderState == OrderState.PLACED) {
                    "Impossible to to expire order with status ${this.orderState}." }
            }
            OrderState.CANCELED -> {
                checkNotNull(id)
                check(this.orderState == OrderState.PLACED) {
                    "Impossible to to cancel order with status ${this.orderState}." }
            }
            OrderState.EXECUTED -> {
                checkNotNull(id)
                check(this.orderState == OrderState.PLACED) {
                    "Impossible to to cancel order with status ${this.orderState}." }
            }
        }
    }
}
