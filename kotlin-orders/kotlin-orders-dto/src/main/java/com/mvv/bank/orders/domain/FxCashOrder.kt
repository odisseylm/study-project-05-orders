package com.mvv.bank.orders.domain


sealed class AbstractFxCashOrder : AbstractOrder<Currency, Quote>() {

    @Suppress("MemberVisibilityCanBePrivate")
    var buyCurrency: Currency? = null
    @Suppress("MemberVisibilityCanBePrivate")
    var sellCurrency: Currency? = null

    // It also can be used to set resultingPrice/resultingQuote from FX rate during order execution (if they are not set).
    // It is optional/temporary (mainly for debugging; most probably after loading order from database it will be lost).
    var resultingRate: FxRate? = null
        set(value) {
            field = value
            if (value != null && resultingPrice == null) {
                // TODO: test with inverted rate
                resultingPrice = value.asPrice(priceCurrency!!, buySellType!!)
            }
            if (value != null && resultingQuote == null) {
                // TODO: test with inverted rate
                resultingQuote = FxRateAsQuote(value, priceCurrency!!)
            }
        }

    private val priceCurrency: Currency? get() = when (buySellType) {
        // opposite
        null -> null
        BuySellType.BUY  -> sellCurrency
        BuySellType.SELL -> buyCurrency
    }

    override var product: Currency?
        get() = when (buySellType) {
            null -> null
            BuySellType.BUY  -> buyCurrency
            BuySellType.SELL -> sellCurrency
        }
        @Deprecated("Better to use buyCurrency/sellCurrency directly.")
        set(value) {
            val buySellType = this.buySellType
            checkNotNull(buySellType) { "buySellType should be set before setting product." }
            when (buySellType) {
                BuySellType.BUY  -> buyCurrency  = value
                BuySellType.SELL -> sellCurrency = value
            }
        }

    fun toExecute(rate: FxRate): Boolean = toExecute(FxRateAsQuote(rate, priceCurrency!!))
}
