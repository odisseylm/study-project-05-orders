package com.mvv.bank.orders.domain

import org.mvv.scala.tools.props.{ ReadOnlyProp, NamedValue, namedValue }

import scala.language.strictEquality
//
import org.slf4j.Logger
import org.slf4j.LoggerFactory
//
import com.mvv.utils.check
import com.mvv.props.checkPropInitialized
import com.mvv.bank.orders.domain.Quote as BaseQuote
import com.mvv.bank.orders.domain.Order


trait LimitOrder[Product, Quote <: BaseQuote] extends Order[Product, Quote] :
  def limitPrice: Amount
  def dailyExecutionType: DailyExecutionType


trait StopOrder[Product, Quote <: BaseQuote] extends Order[Product, Quote] :
  def stopPrice: Amount
  def dailyExecutionType: DailyExecutionType


/** Since java does not support multiple class inheritance common logic for limit and stop orders are put there. */
final class StopLimitOrderSupport[Product, Quote <: BaseQuote] (

  private val order: Order[Product, Quote],

  // TODO: replace with ReadOnlyProp
  private val limitStopPricePropName: String,
  private val limitStopPrice: () => Amount,

  // TODO: replace with ReadOnlyProp
  private val dailyExecutionTypePropName: String = "dailyExecutionType",
  private val dailyExecutionType: () => DailyExecutionType,

  ) :

  type OrderType = com.mvv.bank.orders.domain.Order[Product, Quote]

  def this (
   order: com.mvv.bank.orders.domain.Order[Product, Quote],
   limitStopPrice: ReadOnlyProp[Amount],
   dailyExecutionType: ReadOnlyProp[DailyExecutionType],
  ) =
  this(order, limitStopPrice.name, () => limitStopPrice.value, dailyExecutionType.name, () => dailyExecutionType.value)

  def toExecute(quote: BaseQuote): Boolean =
    val buySellType = order.buySellType
    val limitPrice = limitStopPrice()

    checkPropInitialized(namedValue(buySellType), s"Buy/Sell type is not set for order [${order.id}].")
    checkPropInitialized(namedValue(limitPrice), s"Limit price is not set for order [${order.id}].")

    check(limitPrice.currency == quote.bid.currency,
        s"Quote $quote has incorrect currency ${quote.bid.currency}.")
    check(limitPrice.currency == quote.ask.currency,
        s"Quote $quote has incorrect currency ${quote.bid.currency}.")

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

    order.side match
        //null -> throw IllegalStateException("Order side is not set.")
      case Side.CLIENT => buySellType match
        case BuySellType.SELL => quote.bid.value >= limitPrice.value
        case BuySellType.BUY  => quote.ask.value <= limitPrice.value

      case Side.BANK_MARKET => buySellType match
        case BuySellType.SELL => quote.ask.value >= limitPrice.value
        case BuySellType.BUY  => quote.bid.value <= limitPrice.value
  end toExecute


  def validateCurrentState(): Unit = validateCurrentStopLimitAttributes()
  //noinspection ScalaUnusedSymbol
  def validateNextState(nextState: OrderState): Unit = validateCurrentStopLimitAttributes()

  private def validateCurrentStopLimitAttributes(): Unit =
    if (order.orderState == OrderState.UNKNOWN) { return }

    checkPropInitialized(NamedValue(limitStopPricePropName, limitStopPrice))
    checkPropInitialized(NamedValue(dailyExecutionTypePropName, dailyExecutionType))

end StopLimitOrderSupport
