package com.mvv.bank.orders.domain


enum class OrderType {
    MARKET_ORDER,
    LIMIT_ORDER,
    STOP_ORDER,
    //BUY_STOP_ORDER,
}


enum class BuySellType {
    BUY,
    SELL,
}


enum class DailyExecutionType (val humanName: String) {
    DAY_ONLY("Day Only"),
    GTC("Good 'til Canceled"),
}


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

/*
enum class OrderCancelReason {
    EXPIRED,
    CANCELED_BY_USER,
}
*/
