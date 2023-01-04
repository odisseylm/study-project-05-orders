package com.mvv.bank.orders.rest

import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainCashOrder
import com.mvv.bank.orders.domain.StockOrder as DomainStockOrder
import kotlin.reflect.KClass


enum class OrderType (
    val cashDomainType: KClass<out DomainCashOrder>,
    val stockDomainType: KClass<out DomainStockOrder>,
) {
    MARKET_ORDER(FxCashMarketOrder::class, StockMarketOrder::class),
    LIMIT_ORDER(FxCashLimitOrder::class, StockLimitOrder::class),
    STOP_ORDER(FxCashStopOrder::class, StockStopOrder::class),
}


enum class Side {
    CLIENT,
    BANK_MARKER,
}


enum class BuySellType {
    BUY,
    SELL,
}


enum class DailyExecutionType {
    DAY_ONLY,
    GTC,
}


enum class OrderState {
    UNKNOWN,
    TO_BE_PLACED,
    PLACED,
    EXECUTED,
    EXPIRED,
    CANCELED,
}
