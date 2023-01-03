package com.mvv.bank.orders.rest

import com.mvv.bank.orders.domain.AbstractFxCashOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder
import com.mvv.bank.orders.domain.FxCashStopOrder
import kotlin.reflect.KClass


enum class OrderType (val domainType: KClass<out AbstractFxCashOrder>) {
    MARKET_ORDER(FxCashMarketOrder::class),
    LIMIT_ORDER(FxCashLimitOrder::class),
    STOP_ORDER(FxCashStopOrder::class),
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
