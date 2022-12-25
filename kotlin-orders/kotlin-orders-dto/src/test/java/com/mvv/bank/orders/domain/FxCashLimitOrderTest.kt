package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.Currency.Companion.EUR
import com.mvv.bank.orders.domain.Currency.Companion.UAH
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

private fun bd(v: String): BigDecimal = BigDecimal(v)

class FxCashLimitOrderTest {
    private val date = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val time = LocalTime.of(13, 5)
    private val dateTime = LocalDateTime.of(date, time)

    @Test
    fun toExecuteSellCurrencyOrder() {

        // client wants to sell 20 EUR (another currency UAH) by price >= 39.38

        val uaMarket = TestPredefinedMarkets.KYIV_01
        val limitOrder = FxCashLimitOrder.create(
            side = Side.CLIENT,
            buySellType = BuySellType.SELL,
            sellCurrency = EUR,
            buyCurrency = UAH,
            limitPrice = Amount.of("39.38", UAH),
            executionType = AbstractLimitOrder.ExecutionType.GTC,
            market = uaMarket,
        )

        val rate = FxRate(
            marketSymbol = uaMarket.marketName,
            marketDate = date,
            marketDateTime = dateTime,
            dateTime = ZonedDateTime.of(dateTime, uaMarket.marketZoneId),
            currencyPair = CurrencyPair.of("EUR_UAH"),
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = BigDecimal("39.37"),
            ask = BigDecimal("39.39"),
        )

        // In Foreign Exchange:
        //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
        //  ask - price of client 'buy'  (and dealer/bank 'sell')
        //
        // limit sell price = 39.38
        //
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37"))))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38"))))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39"))))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40"))))
            .isTrue
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41"))))
            .isTrue

        // for inverted rate
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted()))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted()))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted()))
            .isFalse
        // T O D O: it is false due to loosing precision!!! Is it ok, or we should add price rounding
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted()))
            .isFalse
        // 0.0005 is added to bid=39.38 to get bid=39.38 after double inverting
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.3805"), ask = bd("39.40")).inverted()))
            .isTrue
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted()))
            .isTrue
    }

    @Test
    fun toExecuteBuyCurrencyOrder() {

        // client wants to buy 20 EUR (another currency UAH) by price >= 39.38

        val uaMarket = TestPredefinedMarkets.KYIV_01
        val limitOrder = FxCashLimitOrder.create(
            side = Side.CLIENT,
            buySellType = BuySellType.BUY,
            buyCurrency = EUR,
            sellCurrency = UAH,
            limitPrice = Amount.of("39.38", UAH),
            executionType = AbstractLimitOrder.ExecutionType.GTC,
            market = uaMarket,
        )

        val rate = FxRate(
            marketSymbol = uaMarket.marketName,
            marketDate = date,
            marketDateTime = dateTime,
            dateTime = ZonedDateTime.of(dateTime, uaMarket.marketZoneId),
            currencyPair = CurrencyPair.of("EUR_UAH"),
            // In Foreign Exchange:
            //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
            //  ask - price of client 'buy'  (and dealer/bank 'sell')
            bid = BigDecimal("39.37"),
            ask = BigDecimal("39.39"),
        )

        // In Foreign Exchange:
        //  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
        //  ask - price of client 'buy'  (and dealer/bank 'sell')
        //
        // limit sell price = 39.38
        //
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37"))))
            .isTrue
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38"))))
            .isTrue
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39"))))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40"))))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41"))))
            .isFalse

        // for inverted rate
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.35"), ask = bd("39.37")).inverted()))
            .isTrue
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.36"), ask = bd("39.38")).inverted()))
            .isTrue
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.37"), ask = bd("39.39")).inverted()))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.38"), ask = bd("39.40")).inverted()))
            .isFalse
        assertThat(limitOrder.toExecute(rate.copy(bid = bd("39.39"), ask = bd("39.41")).inverted()))
            .isFalse
    }
}
