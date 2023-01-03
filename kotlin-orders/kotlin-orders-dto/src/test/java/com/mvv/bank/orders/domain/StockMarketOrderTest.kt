package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.math.BigDecimal as bd


class StockMarketOrderTest {
    private val market = TestPredefinedMarkets.KYIV1
    private val date = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val time = LocalTime.of(13, 5)
    private val dateTime = LocalDateTime.of(date, time)

    @Test
    fun create() {
        val order = StockMarketOrder.create(
            side = Side.CLIENT,
            buySellType = BuySellType.SELL,
            companySymbol = "AAPL",
            company = TestPredefinedCompanies.APPLE,
            volume = bd("1000"),
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
            bid = Amount.of("0", Currency.USD),
            ask = Amount.of("0", Currency.USD),
        )

        SoftAssertions().apply {

            // mainly to suppress 'unused' warnings
            assertThat(order.orderType).isEqualTo(OrderType.MARKET_ORDER)
            assertThat(order.volume).isEqualTo(bd("1000"))
            assertThat(order.company).isEqualTo(TestPredefinedCompanies.APPLE)

            assertThat(order.toExecute(quote)).isTrue

            assertThatCode { order.toExecute(quote.copy(productSymbol = "GOOGLE")) }
                .hasMessage("This quote is for another product (order: AAPL, quote: GOOGLE).")
                .isExactlyInstanceOf(IllegalStateException::class.java)

        }.assertAll()
    }
}
