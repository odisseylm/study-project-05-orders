package com.mvv.bank.orders.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal as bd
import java.time.ZonedDateTime


class TradesTest {
    private val now = ZonedDateTime.now()

    @Test
    fun createFxCashTrade() {
        val trade = FxCashTrade(
            id = 123L,
            marketSymbol = TestPredefinedMarkets.KYIV1.symbol,
            market = TestPredefinedMarkets.KYIV1,
            buySellType = BuySellType.BUY,
            buyCurrency = Currency.EUR,
            sellCurrency = Currency.UAH,
            volume = bd("1000"),
            price = Amount.of("36.7", Currency.UAH),
            tradedAt = now,
        )

        assertThat(trade.id).isEqualTo(123L)
        assertThat(trade.marketSymbol).isNotNull.isEqualTo(TestPredefinedMarkets.KYIV1.symbol)
        assertThat(trade.market).isEqualTo(TestPredefinedMarkets.KYIV1)
        assertThat(trade.buySellType).isEqualTo(BuySellType.BUY)
        assertThat(trade.buyCurrency).isEqualTo(Currency.EUR)
        assertThat(trade.sellCurrency).isEqualTo(Currency.UAH)

        assertThat((trade as Trade<Currency>).product).isEqualTo(Currency.EUR)
        assertThat(trade.priceCurrency).isEqualTo(Currency.UAH)

        assertThat(trade.volume).isEqualTo(bd(1000))
        assertThat(trade.price).isEqualTo(Amount.of("36.7 UAH"))
        assertThat(trade.tradedAt).isEqualTo(now)
    }

    @Test
    fun createStockTrade() {
        val trade = StockTrade(
            id = 124L,
            marketSymbol = TestPredefinedMarkets.NASDAQ.symbol,
            market = TestPredefinedMarkets.NASDAQ,
            product = TestPredefinedCompanies.APPLE.symbol,
            company = TestPredefinedCompanies.APPLE,
            buySellType = BuySellType.BUY,
            volume = bd("1000"),
            price = Amount.of("95.55", Currency.USD),
            tradedAt = now,
        )

        assertThat(trade.id).isEqualTo(124L)
        assertThat(trade.marketSymbol).isNotNull.isEqualTo(TestPredefinedMarkets.NASDAQ.symbol)
        assertThat(trade.market).isEqualTo(TestPredefinedMarkets.NASDAQ)
        assertThat(trade.buySellType).isEqualTo(BuySellType.BUY)
        assertThat(trade.product).isEqualTo(TestPredefinedCompanies.APPLE.symbol)
        assertThat(trade.company).isEqualTo(TestPredefinedCompanies.APPLE)
        assertThat(trade.volume).isEqualTo(bd(1000))
        assertThat(trade.price).isEqualTo(Amount.of("95.55 USD"))
        assertThat(trade.tradedAt).isEqualTo(now)
    }

    @Test
    fun testTestDateTimeService() {
        val dateTimeService = TestDateTimeService(nowSupplier = { now.toInstant() })
        assertThat(dateTimeService.now()).isEqualTo(now.toInstant())
    }
}
