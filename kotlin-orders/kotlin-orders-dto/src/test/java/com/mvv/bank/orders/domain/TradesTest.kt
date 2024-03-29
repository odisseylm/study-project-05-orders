package com.mvv.bank.orders.domain

import com.mvv.bank.orders.domain.test.TestDateTimeService
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedCompanies
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd
import java.time.ZonedDateTime


internal class TradesTest {
    private val now = ZonedDateTime.now()

    @Test
    fun createFxCashTrade() {
        val trade = FxCashTrade(
            id = 123L,
            market = TestPredefinedMarkets.KYIV1.symbol,
            buySellType = BuySellType.BUY,
            buyCurrency = Currency.EUR,
            sellCurrency = Currency.UAH,
            volume = bd("1000"),
            price = amount("36.7 UAH"),
            tradedAt = now,
        )

        SoftAssertions().apply {

            assertThat(trade.id).isEqualTo(123L)
            assertThat(trade.market).isNotNull.isEqualTo(TestPredefinedMarkets.KYIV1.symbol)
            assertThat(trade.buySellType).isEqualTo(BuySellType.BUY)
            assertThat(trade.buyCurrency).isEqualTo(Currency.EUR)
            assertThat(trade.sellCurrency).isEqualTo(Currency.UAH)

            assertThat((trade as Trade<Currency>).product).isEqualTo(Currency.EUR)
            assertThat(trade.priceCurrency).isEqualTo(Currency.UAH)

            assertThat(trade.volume).isEqualTo(bd(1000))
            assertThat(trade.price).isEqualTo(amount("36.7 UAH"))
            assertThat(trade.tradedAt).isEqualTo(now)

        }.assertAll()
    }

    @Test
    fun createStockTrade() {
        val trade = StockTrade(
            id = 124L,
            market = TestPredefinedMarkets.NASDAQ.symbol,
            company = TestPredefinedCompanies.APPLE.symbol,
            buySellType = BuySellType.BUY,
            volume = bd("1000"),
            price = amount("95.55 USD"),
            tradedAt = now,
        )

        SoftAssertions().apply {

            assertThat(trade.id).isEqualTo(124L)
            assertThat(trade.market).isNotNull.isEqualTo(TestPredefinedMarkets.NASDAQ.symbol)
            assertThat(trade.buySellType).isEqualTo(BuySellType.BUY)
            assertThat(trade.product).isEqualTo(TestPredefinedCompanies.APPLE.symbol)
            assertThat(trade.company).isEqualTo(TestPredefinedCompanies.APPLE.symbol)
            assertThat(trade.volume).isEqualTo(bd(1000))
            assertThat(trade.price).isEqualTo(amount("95.55 USD"))
            assertThat(trade.tradedAt).isEqualTo(now)

        }.assertAll()
    }

    @Test
    fun testTestDateTimeService() {
        val dateTimeService = TestDateTimeService(nowSupplier = { now.toInstant() })
        assertThat(dateTimeService.now()).isEqualTo(now.toInstant())
    }
}
