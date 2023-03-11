package com.mvv.bank.orders.domain

import scala.language.unsafeNulls
//
import scala.math.BigDecimal as bd
//
import java.time.{ LocalDate, LocalTime, ZonedDateTime }
//
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.{ Test, DisplayName }
//
import com.mvv.bank.orders.domain.AbstractCashOrder.Base
import com.mvv.bank.orders.domain.test.predefined.{ TestPredefinedMarkets, TestPredefinedUsers }
import com.mvv.bank.orders.domain.Currency.{ EUR, UAH }
import com.mvv.test.SoftAssertions.runTests


class CashLimitOrderTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val testTime = LocalTime.of(13, 5)
    private val testTimestamp = ZonedDateTime.of(testDate, testTime, testMarket.zoneId)
    private val testUser = TestPredefinedUsers.USER1

    @Test
    @DisplayName("toExecuteSellCurrencyOrder")
    def testToExecuteSellCurrencyOrder(): Unit = {

        // client wants to sell 20 EUR (another currency UAH) by price >= 39.38

        val order = CashLimitOrder(
            Base(
                side = Side.CLIENT,
                user = testUser,
                buySellType = BuySellType.SELL,
                sellCurrency = EUR,
                buyCurrency = UAH,
                volume = bd("1000"),
                market = testMarket,
            ),
            limitPrice = Amount(bd("39.38"), UAH),
            dailyExecutionType = DailyExecutionType.GTC,
        )

        val rate = FxRate(
            testMarket, testTimestamp, currencyPair("EUR_UAH"),
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = bd("39.37"), ask = bd("39.39"),
        )


        SoftAssertions().runTests { a =>

            // mainly to suppress 'unused' warnings
            a.assertThat(order.orderType).isEqualTo(OrderType.LIMIT_ORDER)
            a.assertThat(order.volume).isEqualTo(bd("1000"))
            a.assertThat(order.limitPrice).isEqualTo(amount("39.38 UAH"))
            a.assertThat(order.dailyExecutionType).isEqualTo(DailyExecutionType.GTC)

            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            //
            // limit sell price = 39.38
            //
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37"))))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38"))))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39"))))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40"))))
                .isTrue
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41"))))
                .isTrue

            // for inverted rate
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted))
                .isFalse
            // T O D O: it is false due to loosing precision!!! Is it ok, or we should add price rounding
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted))
                .isFalse
            // 0.0005 is added to bid=39.38 to get bid=39.38 after double inverting
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.3805"), ask = bd("39.40")).inverted))
                .isTrue
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted))
                .isTrue

        }.assertAll()
    }

    @Test
    @DisplayName("toExecuteBuyCurrencyOrder")
    def testToExecuteBuyCurrencyOrder(): Unit = {

        // client wants to buy 20 EUR (another currency UAH) by price >= 39.38

        val order = CashLimitOrder(
            Base(
                side = Side.CLIENT,
                user = testUser,
                buySellType = BuySellType.BUY,
                buyCurrency = EUR,
                sellCurrency = UAH,
                volume = bd("1000"),
                market = testMarket,
            ),
            limitPrice = Amount(bd("39.38"), UAH),
            dailyExecutionType = DailyExecutionType.DAY_ONLY,
        )

        val rate = FxRate(
            market = testMarket.symbol,
            timestamp = testTimestamp,
            marketDate = testDate,
            marketTime = testTime,
            currencyPair = currencyPair("EUR_UAH"),
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = bd("39.37"),
            ask = bd("39.39"),
        )

        SoftAssertions().runTests { a =>

            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            //
            // limit sell price = 39.38
            //
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37"))))
                .isTrue
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38"))))
                .isTrue
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39"))))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40"))))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41"))))
                .isFalse

            // for inverted rate
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted))
                .isTrue
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted))
                .isTrue
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted))
                .isFalse
            a.assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted))
                .isFalse

        }.assertAll()
    }


    @Test
    def validationIsDone(): Unit = {
        assertThatCode { () =>
                CashLimitOrder(
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
            .isExactlyInstanceOf(classOf[IllegalStateException])
    }

    @Test
    def validationIsDone_forBankMarketSide_expectToFail(): Unit = {
        assertThatCode { () =>
                CashLimitOrder(
                    Base(
                        side = Side.BANK_MARKET,
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
            .hasMessage("Currently only client side orders are supported.")
            .isExactlyInstanceOf(classOf[IllegalStateException])
    }


    /*
    // T O D O: do I need this???
    @Test
    def validationIsDoneWithCreatingOrderByDslLikeBuilder(): Unit = {
        assertThatCode {
            createOrder[CashLimitOrder] { o =>
                    o.user = TestPredefinedUsers.USER1
                    o.side = Side.CLIENT
                    o.buySellType = BuySellType.BUY
                    o.buyCurrency = EUR
                    o.sellCurrency = UAH
                    o.volume = bd("1000")
                    o.limitPrice = Amount.of(bd("39.38"), UAH)
                    o.dailyExecutionType = DailyExecutionType.GTC
                    o.market = TestPredefinedMarkets.KYIV1
                    o.orderState = OrderState.EXECUTED
                }
            }
            .hasMessage("Id is not set or incorrect [null].")
            .isExactlyInstanceOf(classOf[IllegalStateException])
    }
    */
}
