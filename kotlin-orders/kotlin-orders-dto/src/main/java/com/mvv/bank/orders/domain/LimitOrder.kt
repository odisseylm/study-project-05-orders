package com.mvv.bank.orders.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger(AbstractLimitOrder::class.java)


enum class DailyExecutionType (val humanName: String) {
    DAY_ONLY("Day Only"),
    GTC("Good 'til Canceled"),
}

interface LimitOrder<Product, Quote> : Order<Product, Quote> {
    var limitPrice: Amount?
    var dailyExecutionType: DailyExecutionType?
}

sealed class AbstractLimitOrder<Product, Quote> : AbstractOrder<Product, Quote>(), LimitOrder<Product, Quote> {

    override val orderType: OrderType = OrderType.LIMIT_ORDER
    override var limitPrice: Amount? = null
    override var dailyExecutionType: DailyExecutionType? = null

    //abstract fun toExecute(currentPrice: FxRate): Boolean

    open fun toExecute(quote: com.mvv.bank.orders.domain.Quote): Boolean {
        val buySellType = this.buySellType
        val limitPrice  = this.limitPrice

        checkNotNull(buySellType) { "Buy/Sell type is not set for order [${id}]." }
        checkNotNull(limitPrice)  { "Limit price is not set for order [${id}]."   }

        check(limitPrice.currency == quote.bid.currency) {
            "Quote $quote has incorrect currency ${quote.bid.currency}." }
        check(limitPrice.currency == quote.ask.currency) {
            "Quote $quote has incorrect currency ${quote.bid.currency}." }

        // For Stock Exchange:
        //  bid - the highest price a buyer (dealer/bank/market) will pay to buy a specified number of shares of a stock
        //  ask - the lowest price at which a seller (dealer/bank/market) will sell the stock
        // The bid price will almost always be lower than the ask or “offer,” price.
        // The market sets bid and ask prices through the placement of buy and sell orders placed by investors,
        // and/or market-makers. If buying demand exceeds selling supply,
        // then often the stock price will rise in the short-term, although that is not guaranteed.
        // Example: bid=$9.95 ask=$10.05
        // See:
        //  https://www.investopedia.com/ask/answers/042215/what-do-bid-and-ask-prices-represent-stock-quote.asp
        //  https://www.investopedia.com/terms/b/bid-and-ask.asp
        //  https://corporatefinanceinstitute.com/resources/equities/bid-and-ask/
        //
        //
        // In Foreign Exchange:
        //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
        //  ask - price of client 'buy'  (and dealer/bank 'sell')

        return when (this.side) {
            null -> throw IllegalStateException("Order side is not set.")
            Side.CLIENT -> when (buySellType) {
                BuySellType.SELL -> quote.bid.amount >= limitPrice.amount
                BuySellType.BUY  -> quote.ask.amount <= limitPrice.amount
            }
            Side.BANK_MARKER -> when (buySellType) {
                BuySellType.SELL -> quote.ask.amount >= limitPrice.amount
                BuySellType.BUY  -> quote.bid.amount <= limitPrice.amount
            }
        }
    }

    override fun validateCurrentState() {
        super.validateCurrentState()

        if (orderState == OrderState.UNKNOWN) {
            return
        }

        checkNotNull(limitPrice)
        checkNotNull(dailyExecutionType)
    }

    override fun validateNextState(nextState: OrderState) {
        super.validateNextState(nextState)

        if (orderState == OrderState.UNKNOWN) {
            return
        }

        checkNotNull(limitPrice)
        checkNotNull(dailyExecutionType)
    }
}
