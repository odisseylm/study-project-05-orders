package com.mvv.bank.orders.rest.entities


enum class OrderType {
    MARKET_ORDER,
    LIMIT_ORDER,
    STOP_ORDER,
}


enum class Side {
    CLIENT,
    BANK_MARKET,
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
