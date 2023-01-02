package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.Currency.Companion.USD
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime


class StockStopOrderTest {
    private val market = TestPredefinedMarkets.KYIV1
    private val date = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val time = LocalTime.of(13, 5)
    private val dateTime = LocalDateTime.of(date, time)

    @Test
    fun toExecuteSellCurrencyOrder() {

        // For example, if the current stock quotation includes a bid of $9.95 and an ask of $10.05,
        // an investor looking to purchase the stock would pay $10.05.
        // An investor looking to sell the stock would sell it at $10.
        //
        // bid = $9.95, ask = $10.05

        // client wants to sell 20 Apple shares (another currency UAH) by price >= 10.00

        val order = StockStopOrder.create(
            side = Side.CLIENT,
            buySellType = BuySellType.SELL,
            companySymbol = "AAPL",
            company = TestPredefinedCompanies.APPLE,
            volume = bd("1000"),
            stopPrice = Amount.of("10.00", USD),
            dailyExecutionType = DailyExecutionType.DAY_ONLY,
            marketSymbol = market.symbol,
            market = market,
        )

        // mainly to suppress 'unused' warnings
        assertThat(order.orderType).isEqualTo(OrderType.STOP_ORDER)
        assertThat(order.volume).isEqualTo(bd("1000"))
        assertThat(order.stopPrice).isEqualTo(Amount.of("10.00 USD"))
        assertThat(order.dailyExecutionType).isEqualTo(DailyExecutionType.DAY_ONLY)
        assertThat(order.company).isEqualTo(TestPredefinedCompanies.APPLE)

        val quote = StockQuote(
            marketSymbol = market.symbol,
            dateTime = ZonedDateTime.of(dateTime, market.zoneId),
            marketDate = date,
            marketTime = time,
            productSymbol = "AAPL",
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = Amount.of("0", USD),
            ask = Amount.of("0", USD),
        )

        assertThat(order.toExecute(quote.copy(bid = Amount.of("9.85", USD), ask = Amount.of("9.95", USD))))
            .isFalse // because market price for client sell 10.05 < my desired limit sell price 10.00
        assertThat(order.toExecute(quote.copy(bid = Amount.of("9.90", USD), ask = Amount.of("10.00", USD))))
            .isFalse // because market price for client sell 10.05 < my desired limit sell price 10.00
        assertThat(order.toExecute(quote.copy(bid = Amount.of("9.95", USD), ask = Amount.of("10.05", USD))))
            .isFalse // because market price for client sell 9.95 < my desired limit sell price 10.00

        assertThat(order.toExecute(quote.copy(bid = Amount.of("10.00", USD), ask = Amount.of("10.10", USD))))
            .isTrue // because market price for client sell 10.00 >= my desired limit sell price 10.00
        assertThat(order.toExecute(quote.copy(bid = Amount.of("10.05", USD), ask = Amount.of("10.15", USD))))
            .isTrue // because market price for client sell 10.05 > my desired limit sell price 10.00
    }

    @Test
    fun toExecuteBuyCurrencyOrder() {

        // For example, if the current stock quotation includes a bid of $9.95 and an ask of $10.05,
        // an investor looking to purchase the stock would pay $10.05.
        // An investor looking to sell the stock would sell it at $10.
        //
        // bid = $9.95, ask = $10.05

        // client wants to sell 20 Apple shares (another currency UAH) by price >= 10.00

        val order = StockStopOrder.create(
            side = Side.CLIENT,
            buySellType = BuySellType.BUY,
            companySymbol = "AAPL",
            company = TestPredefinedCompanies.APPLE,
            volume = bd("1000"),
            stopPrice = Amount.of("10.00", USD),
            dailyExecutionType = DailyExecutionType.GTC,
            marketSymbol = market.symbol,
            market = market,
        )

        val quote = StockQuote(
            marketSymbol = market.symbol,
            dateTime = ZonedDateTime.of(dateTime, market.zoneId),
            marketDate = date,
            marketTime = time,
            productSymbol = "AAPL",
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = Amount.of("0", USD),
            ask = Amount.of("0", USD),
        )

        assertThat(order.toExecute(quote.copy(bid = Amount.of("9.85", USD), ask = Amount.of("9.95", USD))))
            .isTrue // because market price for client buy 9.95 < my desired limit buy price 10.00
        assertThat(order.toExecute(quote.copy(bid = Amount.of("9.90", USD), ask = Amount.of("10.00", USD))))
            .isTrue // because market price for client buy 10.00 <= my desired limit buy price 10.00

        assertThat(order.toExecute(quote.copy(bid = Amount.of("9.95", USD), ask = Amount.of("10.05", USD))))
            .isFalse // because market price for client buy 10.05 > my desired limit buy price 10.00
        assertThat(order.toExecute(quote.copy(bid = Amount.of("10.00", USD), ask = Amount.of("10.10", USD))))
            .isFalse // because market price for client buy 10.00 > my desired limit buy price 10.00
        assertThat(order.toExecute(quote.copy(bid = Amount.of("10.05", USD), ask = Amount.of("10.15", USD))))
            .isFalse // because market price for client buy 10.05 > my desired limit buy price 10.00
    }
}
