package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.AbstractCashOrder.Base
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedUsers

import com.mvv.bank.orders.domain.Currency.Companion.EUR
import com.mvv.bank.orders.domain.Currency.Companion.UAH
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


internal class CashStopOrderTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val testTime = LocalTime.of(13, 5)
    private val testTimestamp = ZonedDateTime.of(testDate, testTime, testMarket.zoneId)
    private val testUser = TestPredefinedUsers.USER1

    @Test
    fun toExecuteSellCurrencyOrder() {

        // client wants to sell 20 EUR (another currency UAH) by price >= 39.38

        val order = CashStopOrder.create(
            Base(
                side = Side.CLIENT,
                user = testUser,
                buySellType = BuySellType.SELL,
                sellCurrency = EUR,
                buyCurrency = UAH,
                volume = bd("1000"),
                market = testMarket,
            ),
            stopPrice = amount("39.38 UAH"),
            dailyExecutionType = DailyExecutionType.GTC,
        )

        SoftAssertions().apply {

            assertThat(order.orderType).isEqualTo(OrderType.STOP_ORDER)

            val rate = FxRate(
                testMarket, testTimestamp, currencyPair("EUR_UAH"),
                // In Foreign Exchange:
                //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
                //  ask - price of client 'buy'  (and dealer/bank 'sell')
                bid = bd("39.37"), ask = bd("39.39"),
            )

            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            //
            // limit sell price = 39.38
            //
            assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37"))))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38"))))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39"))))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40"))))
                .isTrue
            assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41"))))
                .isTrue

            // for inverted rate
            assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted))
                .isFalse
            // T O D O: it is false due to loosing precision!!! Is it ok, or we should add price rounding
            assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted))
                .isFalse
            // 0.0005 is added to bid=39.38 to get bid=39.38 after double inverting
            assertThat(order.toExecute(rate.copy(bid = bd("39.3805"), ask = bd("39.40")).inverted))
                .isTrue
            assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted))
                .isTrue

        }.assertAll()
    }

    @Test
    fun toExecuteBuyCurrencyOrder() {

        // client wants to buy 20 EUR (another currency UAH) by price >= 39.38

        val order = CashStopOrder.create(
            Base(
                side = Side.CLIENT,
                user = testUser,
                buySellType = BuySellType.BUY,
                buyCurrency = EUR,
                sellCurrency = UAH,
                volume = bd("1000"),
                market = testMarket,
            ),
            stopPrice = amount("39.38 UAH"),
            dailyExecutionType = DailyExecutionType.GTC,
        )

        val rate = FxRate(
            testMarket, testTimestamp, currencyPair("EUR_UAH"),
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = bd("39.37"), ask = bd("39.39"),
        )

        SoftAssertions().apply {

            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            //
            // limit sell price = 39.38
            //
            assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37"))))
                .isTrue
            assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38"))))
                .isTrue
            assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39"))))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40"))))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41"))))
                .isFalse

            // for inverted rate
            assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted))
                .isTrue
            assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted))
                .isTrue
            assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted))
                .isFalse
            assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted))
                .isFalse

        }.assertAll()
    }


    @Test
    fun validationIsDone() {
        assertThatCode {
                CashLimitOrder.create(
                    Base(
                        side = Side.CLIENT,
                        user = testUser,
                        buySellType = BuySellType.BUY,
                        buyCurrency = EUR,
                        sellCurrency = UAH,
                        volume = bd("1000"),
                        market = TestPredefinedMarkets.KYIV1,
                        orderState = OrderState.EXECUTED,
                    ),
                    limitPrice = amount("39.38 UAH"),
                    dailyExecutionType = DailyExecutionType.GTC,
                )
            }
            .hasMessage("Id is not set or incorrect [null].")
            .isExactlyInstanceOf(IllegalStateException::class.java)
    }
}
