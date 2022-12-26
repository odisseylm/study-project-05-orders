package com.mvv.bank.orders.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.mvv.bank.orders.domain.Quote as BaseQuote

@Suppress("unused")
private val log: Logger = LoggerFactory.getLogger(LimitOrder::class.java)


interface LimitOrder<Product: Any, Quote: BaseQuote> : Order<Product, Quote> {
    var buySellType: BuySellType
    var limitPrice: Amount
    var dailyExecutionType: DailyExecutionType
}


class LimitOrderSupport<Product: Any, Quote: BaseQuote> { //}: AbstractOrder<Product, Quote>(), LimitOrder<Product, Quote> {

    fun toExecute(order: LimitOrder<Product, Quote>, quote: Quote): Boolean {
        val buySellType = order.buySellType
        val limitPrice  = order.limitPrice

        checkNotNull(buySellType) { "Buy/Sell type is not set for order [${order.id}]." }
        checkNotNull(limitPrice)  { "Limit price is not set for order [${order.id}]."   }

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

        return when (order.side) {
            //null -> throw IllegalStateException("Order side is not set.")
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

    fun validateCurrentState(order: LimitOrder<*, *>) {
        //super.validateCurrentState()

        if (order.orderState == OrderState.UNKNOWN) {
            return
        }

        checkNotNull(order.limitPrice)
        checkNotNull(order.dailyExecutionType)
    }

    @Suppress("UNUSED_PARAMETER")
    fun validateNextState(order: LimitOrder<*, *>, nextState: OrderState) {
        //super.validateNextState(nextState)

        if (order.orderState == OrderState.UNKNOWN) {
            return
        }

        checkNotNull(order.limitPrice)
        checkNotNull(order.dailyExecutionType)
    }
}
