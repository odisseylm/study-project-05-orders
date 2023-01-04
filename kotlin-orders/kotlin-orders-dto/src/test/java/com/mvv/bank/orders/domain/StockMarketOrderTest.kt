package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.math.BigDecimal as bd


class StockMarketOrderTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val testTime = LocalTime.of(13, 5)
    private val testDateTime = LocalDateTime.of(testDate, testTime)
    private val testUser = TestPredefinedUsers.USER1
    private val testCompany = TestPredefinedCompanies.APPLE

    @Test
    fun create() {
        val order = StockMarketOrder.create(
            user = testUser,
            side = Side.CLIENT,
            buySellType = BuySellType.SELL,
            companySymbol = testCompany.symbol,
            company = TestPredefinedCompanies.APPLE,
            volume = bd("1000"),
            marketSymbol = testMarket.symbol,
            market = testMarket,
        )

        val quote = StockQuote(
            marketSymbol = testMarket.symbol,
            dateTime = ZonedDateTime.of(testDateTime, testMarket.zoneId),
            marketDate = testDate,
            marketTime = testTime,
            productSymbol = testCompany.symbol,
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
