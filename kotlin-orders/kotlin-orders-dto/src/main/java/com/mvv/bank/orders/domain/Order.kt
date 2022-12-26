package com.mvv.bank.orders.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime


private val log: Logger = LoggerFactory.getLogger(Order::class.java)

/**
 * I am not 100% sure that is the same as Buy-Side/Sell-Side, but seems so :-)
 * https://corporatefinanceinstitute.com/resources/career/buy-side-vs-sell-side/
 */
enum class Side {
    /**
     * Trade/orders for clients.
     *
     * Buy-Side according to https://corporatefinanceinstitute.com/resources/career/buy-side-vs-sell-side/
     * Buy-Side – is the side of the financial market that buys and invests large portions of securities for the purpose of money or fund management.
     * The Buy Side refers to firms that purchase securities and includes investment managers, pension funds, and hedge funds.
     */
    CLIENT,

    /**
     * Bank or market.
     * Sell-Side according to https://corporatefinanceinstitute.com/resources/career/buy-side-vs-sell-side/
     * Sell-Side – is the other side of the financial market, which deals with the creation, promotion,
     * and selling of traded securities to the public.
     * The Sell-Side refers to firms that issue, sell, or trade securities, and includes investment banks,
     * advisory firms, and corporations.
     */
    BANK_MARKER, // bank or market
}


interface DateTimeService {
    val clock: Clock
    fun now(): Instant
}

interface GeneralContext {
    val dateTimeService: DateTimeService
}

interface OrderContext : GeneralContext {
    val market: Market
    fun now(): Instant = dateTimeService.now()
    fun nowOnMarket(): LocalDateTime = LocalDateTime.ofInstant(now(), market.zoneId)

    companion object {
        fun create(
            dateTimeService: DateTimeService,
            market: Market,
        ) : OrderContext = OrderContextImpl(
            dateTimeService = dateTimeService,
            market = market,
        )
    }
}


private class OrderContextImpl (
    override val dateTimeService: DateTimeService,
    override val market: Market,
) : OrderContext

enum class OrderState {
    UNKNOWN,
    TO_BE_PLACED,
    PLACED,
    EXECUTED,
    EXPIRED,
    CANCELED,
}

/*
// optional API (can be removed)
val OrderState.asVerb get() =
    when (this) {
        OrderState.UNKNOWN  -> "make unknown"
        OrderState.PLACED   -> "place"
        OrderState.EXECUTED -> "execute"
        OrderState.CANCELED -> "cancel"
        OrderState.EXPIRED  -> "expire"
    }
*/

enum class OrderCancelReason {
    EXPIRED,
    CANCELED_BY_USER,
}

sealed interface Order<Product, Quote> {
    var id: Long?
    var side: Side?
    val orderType: OrderType
    var product: Product?

    // several variables are used to see problems in case of signal race abd if both
    // operations are happened 'cancel' and 'execute/expire'
    var placedAt: Instant?
    var executedAt: Instant?
    var canceledAt: Instant?
    var expiredAt: Instant?

    var market: Market?

    var orderState: OrderState

    var resultingPrice: Amount?
    // it is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost)
    var resultingQuote: Quote?

    // you can change state only to proper next valid state
    fun changeOrderState(nextOrderState: OrderState, context: OrderContext)
    fun validateCurrentState()
    fun validateNextState(nextState: OrderState)
}


sealed class AbstractOrder<Product, Quote> : Order<Product, Quote> {
    override var id: Long? = null
    override var side: Side? = null
        set(value) {
            if (field != null && value != field) throw IllegalStateException("Changing order side is not allowed (from $field to $value).")
            field = value
        }
    override var product: Product? = null
    override var market: Market? = null

    var buySellType: BuySellType? = null
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

    /*
    var resultingPrice: Amount? = resultingPrice
        set(value) {
            check( field == null || field == resultingPrice ) {
                "Resulting price cannot be changed (from ${this.resultingPrice} to $value)"
            }

            // TODO: uncomment/implement
            //if (value != null) {
            //    check(value.currencyPair == this.currencyPair) {
            //        "Invalid currency pair ${value.currencyPair} of resulting price."
            //    }
            //}

            field = value
        }
        */

    override fun validateCurrentState() {
        if (orderState == OrderState.UNKNOWN) {
            log.warn("Attempt to validate current state of order with status $orderState") // TODO: fix warning message
            return
        }

        @Suppress("RedundantRequireNotNullCall")
        checkNotNull(orderType)
        checkNotNull(side)
        check(side == Side.CLIENT) { "Currently only client side orders are supported." }
        checkNotNull(product)
        checkNotNull(market)
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
        checkNotNull(product)
        checkNotNull(market)
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
