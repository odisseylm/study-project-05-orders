package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.Currency.Companion.EUR
import com.mvv.bank.orders.domain.Currency.Companion.UAH
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.math.BigDecimal as bd


class FxCashLimitOrderTest {
    private val market = TestPredefinedMarkets.KYIV1
    private val date = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val time = LocalTime.of(13, 5)
    private val dateTime = LocalDateTime.of(date, time)
    private val zonedDateTime = ZonedDateTime.of(dateTime, market.zoneId)

    @Test
    fun toExecuteSellCurrencyOrder() {

        // client wants to sell 20 EUR (another currency UAH) by price >= 39.38

        val order = FxCashLimitOrder.create(
            side = Side.CLIENT,
            buySellType = BuySellType.SELL,
            sellCurrency = EUR,
            buyCurrency = UAH,
            volume = bd("1000"),
            limitPrice = Amount.of("39.38", UAH),
            dailyExecutionType = DailyExecutionType.GTC,
            marketSymbol = market.symbol,
            market = market,
        )

        // mainly to suppress 'unused' warnings
        assertThat(order.orderType).isEqualTo(OrderType.LIMIT_ORDER)
        assertThat(order.volume).isEqualTo(bd("1000"))
        assertThat(order.limitPrice).isEqualTo(Amount.of("39.38 UAH"))
        assertThat(order.dailyExecutionType).isEqualTo(DailyExecutionType.GTC)

        val rate = FxRate(
            market, zonedDateTime, CurrencyPair.of("EUR_UAH"),
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
        assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted()))
            .isFalse
        assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted()))
            .isFalse
        assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted()))
            .isFalse
        // T O D O: it is false due to loosing precision!!! Is it ok, or we should add price rounding
        assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted()))
            .isFalse
        // 0.0005 is added to bid=39.38 to get bid=39.38 after double inverting
        assertThat(order.toExecute(rate.copy(bid = bd("39.3805"), ask = bd("39.40")).inverted()))
            .isTrue
        assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted()))
            .isTrue
    }

    @Test
    fun toExecuteBuyCurrencyOrder() {

        // client wants to buy 20 EUR (another currency UAH) by price >= 39.38

        val order = FxCashLimitOrder.create(
            side = Side.CLIENT,
            buySellType = BuySellType.BUY,
            buyCurrency = EUR,
            sellCurrency = UAH,
            volume = bd("1000"),
            limitPrice = Amount.of("39.38", UAH),
            dailyExecutionType = DailyExecutionType.GTC,
            marketSymbol = market.symbol,
            market = market,
        )

        val rate = FxRate(
            marketSymbol = market.symbol,
            dateTime = zonedDateTime,
            marketDate = date,
            marketTime = time,
            currencyPair = CurrencyPair.of("EUR_UAH"),
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = bd("39.37"),
            ask = bd("39.39"),
        )

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
        assertThat(order.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted()))
            .isTrue
        assertThat(order.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted()))
            .isTrue
        assertThat(order.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted()))
            .isFalse
        assertThat(order.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted()))
            .isFalse
        assertThat(order.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted()))
            .isFalse
    }


    @Test
    fun validationIsDone() {
        assertThatCode {
                FxCashLimitOrder.create(
                    side = Side.CLIENT,
                    buySellType = BuySellType.BUY,
                    buyCurrency = EUR,
                    sellCurrency = UAH,
                    volume = bd("1000"),
                    limitPrice = Amount.of("39.38", UAH),
                    dailyExecutionType = DailyExecutionType.GTC,
                    marketSymbol = market.symbol,
                    market = TestPredefinedMarkets.KYIV1,
                    orderState = OrderState.EXECUTED,
                )
            }
            .hasMessage("Id is not set or incorrect null.")
            .isExactlyInstanceOf(IllegalStateException::class.java)
    }


    @Test
    fun validationIsDoneWithCreatingOrderByDslLikeBuilder() {
        assertThatCode {
            createOrder<FxCashLimitOrder> {
                    side = Side.CLIENT
                    buySellType = BuySellType.BUY
                    buyCurrency = EUR
                    sellCurrency = UAH
                    volume = bd("1000")
                    limitPrice = Amount.of("39.38", UAH)
                    dailyExecutionType = DailyExecutionType.GTC
                    marketSymbol = TestPredefinedMarkets.KYIV1.symbol
                    market = TestPredefinedMarkets.KYIV1
                    orderState = OrderState.EXECUTED
                }
            }
            .hasMessage("Id is not set or incorrect null.")
            .isExactlyInstanceOf(IllegalStateException::class.java)
    }
}
