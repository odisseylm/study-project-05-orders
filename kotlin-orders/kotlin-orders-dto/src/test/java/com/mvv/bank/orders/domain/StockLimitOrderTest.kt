package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.test.predefined.TestPredefinedCompanies
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedUsers
import com.mvv.bank.orders.domain.AbstractStockOrder.Base
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


internal class StockLimitOrderTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val testTime = LocalTime.of(13, 5)
    private val testTimestamp = ZonedDateTime.of(testDate, testTime, testMarket.zoneId)
    private val testUser = TestPredefinedUsers.USER1
    private val testCompany = TestPredefinedCompanies.APPLE

    @Test
    fun toExecuteSellCurrencyOrder() {

        // For example, if the current stock quotation includes a bid of $9.95 and an ask of $10.05,
        // an investor looking to purchase the stock would pay $10.05.
        // An investor looking to sell the stock would sell it at $10.
        //
        // bid = $9.95, ask = $10.05

        // client wants to sell 20 Apple shares (another currency UAH) by price >= 10.00

        val order = StockLimitOrder.create(
            Base(
                side = Side.CLIENT,
                user = testUser,
                buySellType = BuySellType.SELL,
                company = testCompany,
                volume = bd("1000"),
                market = testMarket,
            ),
            limitPrice = amount("10.00 USD"),
            dailyExecutionType = DailyExecutionType.DAY_ONLY,
        )

        val quote = StockQuote(
            market = testMarket.symbol,
            company = testCompany.symbol,
            marketDate = testDate,
            marketTime = testTime,
            timestamp = testTimestamp ,
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = amount("0 USD"),
            ask = amount("0 USD"),
        )

        SoftAssertions().apply {

            // mainly to suppress 'unused' warnings
            assertThat(order.orderType).isEqualTo(OrderType.LIMIT_ORDER)
            assertThat(order.volume).isEqualTo(bd("1000"))
            assertThat(order.limitPrice).isEqualTo(Amount.valueOf("10.00 USD"))
            assertThat(order.dailyExecutionType).isEqualTo(DailyExecutionType.DAY_ONLY)
            assertThat(order.company).isEqualTo(testCompany)

            assertThat(order.toExecute(quote.copy(bid = amount("9.85 USD"), ask = amount("9.95 USD"))))
                .isFalse // because market price for client sell 10.05 < my desired limit sell price 10.00
            assertThat(order.toExecute(quote.copy(bid = amount("9.90 USD"), ask = amount("10.00 USD"))))
                .isFalse // because market price for client sell 10.05 < my desired limit sell price 10.00
            assertThat(order.toExecute(quote.copy(bid = amount("9.95 USD"), ask = amount("10.05 USD"))))
                .isFalse // because market price for client sell 9.95 < my desired limit sell price 10.00

            assertThat(order.toExecute(quote.copy(bid = amount("10.00 USD"), ask = amount("10.10 USD"))))
                .isTrue // because market price for client sell 10.00 >= my desired limit sell price 10.00
            assertThat(order.toExecute(quote.copy(bid = amount("10.05 USD"), ask = amount("10.15 USD"))))
                .isTrue // because market price for client sell 10.05 > my desired limit sell price 10.00

        }.assertAll()
    }

    @Test
    fun toExecuteBuyCurrencyOrder() {

        // For example, if the current stock quotation includes a bid of $9.95 and an ask of $10.05,
        // an investor looking to purchase the stock would pay $10.05.
        // An investor looking to sell the stock would sell it at $10.
        //
        // bid = $9.95, ask = $10.05

        // client wants to sell 20 Apple shares (another currency UAH) by price >= 10.00

        val order = StockLimitOrder.create(
            Base(
                side = Side.CLIENT,
                user = testUser,
                buySellType = BuySellType.BUY,
                company = testCompany,
                volume = bd("1000"),
                market = testMarket,
            ),
            limitPrice = amount("10.00 USD"),
            dailyExecutionType = DailyExecutionType.GTC,
        )

        val quote = StockQuote(
            market = testMarket.symbol,
            company = testCompany.symbol,
            timestamp = testTimestamp,
            marketDate = testDate,
            marketTime = testTime,
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = amount("0 USD"),
            ask = amount("0 USD"),
        )

        SoftAssertions().apply {

            assertThat(order.toExecute(quote.copy(bid = amount("9.85 USD"), ask = amount("9.95 USD"))))
                .isTrue // because market price for client buy 9.95 < my desired limit buy price 10.00
            assertThat(order.toExecute(quote.copy(bid = amount("9.90 USD"), ask = amount("10.00 USD"))))
                .isTrue // because market price for client buy 10.00 <= my desired limit buy price 10.00

            assertThat(order.toExecute(quote.copy(bid = amount("9.95 USD"), ask = amount("10.05 USD"))))
                .isFalse // because market price for client buy 10.05 > my desired limit buy price 10.00
            assertThat(order.toExecute(quote.copy(bid = amount("10.00 USD"), ask = amount("10.10 USD"))))
                .isFalse // because market price for client buy 10.00 > my desired limit buy price 10.00
            assertThat(order.toExecute(quote.copy(bid = amount("10.05 USD"), ask = amount("10.15 USD"))))
                .isFalse // because market price for client buy 10.05 > my desired limit buy price 10.00

        }.assertAll()
    }
}
