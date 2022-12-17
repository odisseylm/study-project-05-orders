package com.mvv.bank.orders.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

private val log: Logger = LoggerFactory.getLogger(Order::class.java)


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
    fun nowOnMarket(): LocalDateTime = LocalDateTime.ofInstant(now(), market.marketZoneId)

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
    PLACED,
    EXECUTED,
    EXPIRED,
    CANCELED,
}

// optional API (can be removed)
val OrderState.asVerb get() =
    when (this) {
        OrderState.UNKNOWN  -> "make unknown"
        OrderState.PLACED   -> "place"
        OrderState.EXECUTED -> "execute"
        OrderState.CANCELED -> "cancel"
        OrderState.EXPIRED  -> "expire"
    }

enum class OrderCancelReason {
    EXPIRED,
    CANCELED_BY_USER,
}

interface Order {
    val orderType: OrderType
    val id: Long?
    val currencyPair: CurrencyPair

    // read-only properties since they will be set/filled by external system
    // or at least can be initially set by changeOrderState()
    val placedAt: Instant?
    val market: Market?

    val orderState: OrderState

    // you can change state only to proper next valid state
    fun changeOrderState(orderState: OrderState, context: OrderContext)
}

abstract class BaseOrder (
    override val id: Long?,
    override val currencyPair: CurrencyPair,
    orderState: OrderState,
    placedAt: Instant?,
    market: Market?,
    resultingPrice: FxRate? = null,
) : Order {

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

    // TODO: probably is better to split to separate methods???
    override fun changeOrderState(orderState: OrderState, context: OrderContext) {
        val currentOrderState = this.orderStateValue
        if (orderState == currentOrderState) {
            log.warn("Attempt to set the same state for order $id.") // T O D O: probably not needed
            return
        }

        when (orderState) {
            OrderState.UNKNOWN -> throw IllegalStateException("Impossible to change state to $orderState.")
            OrderState.PLACED -> {
                check(currentOrderState == OrderState.UNKNOWN) {
                    "Only order with state 'unknown' can be changed to 'created'." }
                this.placedAtValue = context.now()
                this.marketValue = context.market
            }
            OrderState.EXECUTED, OrderState.CANCELED, OrderState.EXPIRED ->
                check(currentOrderState == OrderState.UNKNOWN) {
                    "Impossible to ${orderState.asVerb} order with state $currentOrderState." }
        }

        this.orderStateValue = orderState
    }

    var resultingPrice: FxRate? = resultingPrice
        set(value) {
            check( field == null || field == resultingPrice ) {
                "Resulting price cannot be changed (from ${this.resultingPrice} to $value)"
            }

            if (value != null) {
                check(value.currencyPair == this.currencyPair) {
                    "Invalid currency pair ${value.currencyPair} of resulting price."
                }
            }

            field = value
        }

}
