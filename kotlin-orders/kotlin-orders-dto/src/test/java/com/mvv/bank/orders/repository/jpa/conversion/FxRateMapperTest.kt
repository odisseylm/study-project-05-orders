package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.domain.Currency
import com.mvv.bank.orders.domain.CurrencyPair
import com.mvv.bank.orders.domain.TestPredefinedMarkets
import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
import com.mvv.bank.orders.domain.FxRate as DomainFxRate
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.math.BigDecimal as bd
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime


internal class CurrencyMapperTest {

    @Test
    fun currencyMapping() {
        val currencyMapper: CurrencyMapper = Mappers.getMapper(CurrencyMapper::class.java)

        SoftAssertions().apply {

            assertThat(currencyMapper).isNotNull

            assertThat(currencyMapper.toDto(null)).isNull()

            assertThat(currencyMapper.fromDto("USD")).isEqualTo(Currency.of("USD"))
            assertThat(currencyMapper.fromDto("USD")).isEqualTo(Currency.USD)

            assertThat(currencyMapper.toDto(Currency.of("USD"))).isEqualTo("USD")
            assertThat(currencyMapper.toDto(Currency.USD)).isEqualTo("USD")

            assertThatCode { currencyMapper.fromDto("USD ") }
                .hasMessage("Invalid currency [USD ].")
                .isExactlyInstanceOf(IllegalArgumentException::class.java)

        }.assertAll()
    }

}



internal class FxRateMapperTest {
    private val market = TestPredefinedMarkets.KYIV1
    private val date = LocalDate.now()
    private val time = LocalTime.now()
    private val dateTime = LocalDateTime.of(date, time)
    private val zonedDateTime = ZonedDateTime.of(dateTime, market.zoneId)

    @Test
    fun toDto() {
        val mapper = Mappers.getMapper(FxRateMapper::class.java)

        SoftAssertions().apply {

            assertThat(mapper).isNotNull

            val dto = mapper.toDto(
                DomainFxRate(
                    market, zonedDateTime, CurrencyPair.EUR_USD,
                    bid = bd("1.1"), ask = bd("1.2"),
                )
            )

            assertThat(dto).isNotNull

            checkNotNull(dto)
            assertThat(dto.cur1).isEqualTo("EUR")
            assertThat(dto.cur2).isEqualTo("USD")
            assertThat(dto.marketSymbol).isNotNull.isEqualTo(market.symbol)
            assertThat(dto.marketDate).isNotNull.isEqualTo(date)
            assertThat(dto.marketTime).isNotNull.isEqualTo(time)
            assertThat(dto.dateTime).isNotNull.isEqualTo(zonedDateTime)
            assertThat(dto.bid).isEqualTo(bd("1.1"))
            assertThat(dto.ask).isEqualTo(bd("1.2"))

        }.assertAll()
    }

    @Test
    fun fromDto() {
        val mapper = Mappers.getMapper(FxRateMapper::class.java)
        val test = this // to safely use it from 'apply'

        SoftAssertions().apply {

            assertThat(mapper).isNotNull

            val domainObj = mapper.fromDto(
                JpaFxRate().apply {
                    marketSymbol = test.market.symbol
                    dateTime   = test.zonedDateTime
                    marketDate = test.date
                    marketTime = test.time
                    cur1 = "EUR"
                    cur2 = "USD"
                    bid = bd("1.1")
                    ask = bd("1.2")
                }
            )

            assertThat(domainObj).isNotNull

            checkNotNull(domainObj) // for kotlin only
            assertThat(domainObj.currencyPair).isEqualTo(CurrencyPair.of("EUR_USD"))
            assertThat(domainObj.marketSymbol).isNotNull.isEqualTo(market.symbol)
            assertThat(domainObj.marketDate).isNotNull.isEqualTo(date)
            assertThat(domainObj.marketTime).isNotNull.isEqualTo(time)
            assertThat(domainObj.dateTime).isNotNull.isEqualTo(zonedDateTime)
            assertThat(domainObj.bid).isEqualTo(bd("1.1"))
            assertThat(domainObj.ask).isEqualTo(bd("1.2"))

        }.assertAll()
    }
}
