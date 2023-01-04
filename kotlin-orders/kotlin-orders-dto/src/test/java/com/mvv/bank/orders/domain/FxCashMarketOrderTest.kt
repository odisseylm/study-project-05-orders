package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedUsers

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime


class FxCashMarketOrderTest {
    private val market = TestPredefinedMarkets.KYIV1
    private val date = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val time = LocalTime.of(13, 5)
    private val dateTime = LocalDateTime.of(date, time)
    private val zonedDateTime = ZonedDateTime.of(dateTime, market.zoneId)
    private val testUser = TestPredefinedUsers.USER1

    @Test
    fun create() {
        val order = FxCashMarketOrder.create(
            side = Side.CLIENT,
            user = testUser,
            buySellType = BuySellType.SELL,
            buyCurrency = Currency.USD,
            sellCurrency = Currency.EUR,
            volume = bd("1000"),
            marketSymbol = market.symbol,
            market = market,
        )

        SoftAssertions().apply {

            assertThat(order.orderType).isEqualTo(OrderType.MARKET_ORDER)

            assertThat(
                order.toExecute(FxRate.of(market, zonedDateTime, CurrencyPair.EUR_USD, bid = bd("1.1"), ask = bd("1.15")))
                ).isTrue

            assertThatCode {
                order.toExecute(FxRate.of(market, zonedDateTime, CurrencyPair.EUR_UAH, bid = bd("1.1"), ask = bd("1.15")))
                }
                .hasMessage("FX rate EUR_UAH does not suite order currencies (with price currency USD).")
                .isExactlyInstanceOf(IllegalStateException::class.java)

            assertThatCode {
                order.toExecute(
                    FxRate.of(market, zonedDateTime, CurrencyPair.USD_UAH, bid = bd("1.1"), ask = bd("1.15")))
                }
                .hasMessage("FX rate currencies UAH_USD does not suite order currencies USD_EUR.")
                .isExactlyInstanceOf(IllegalStateException::class.java)

        }.assertAll()
    }
}