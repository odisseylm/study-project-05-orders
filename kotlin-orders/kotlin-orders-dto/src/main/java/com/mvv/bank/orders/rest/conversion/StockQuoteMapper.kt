package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.domain.CompanySymbol
import com.mvv.bank.orders.domain.MarketSymbol
import com.mvv.bank.orders.domain.StockQuote as DomainStockQuote
import com.mvv.bank.orders.rest.entities.StockQuote as DtoStockQuote
import org.mapstruct.Mapper

import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.Amount as DomainAmount


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    uses = [AmountMapper::class]
)
@Suppress("CdiInjectionPointsInspection")
interface StockQuoteMapper {
    fun toDto(quote: DomainStockQuote?): DtoStockQuote?

    fun toDomain(quote: DtoStockQuote?): DomainStockQuote? = if (quote == null) null
        else DomainStockQuote(
            market = MarketSymbol.of(quote.market), company = CompanySymbol.of(quote.company),
            timestamp = quote.timestamp, marketDate = quote.marketDate, marketTime = quote.marketTime,
            bid = DomainAmount.of(quote.bid.value, DomainCurrency.of(quote.bid.currency)),
            ask = DomainAmount.of(quote.ask.value, DomainCurrency.of(quote.ask.currency)),
            )

    /*
    // T O D O: write easier solution
    @Mapping(target = "last", ignore = true)
    @Mapping(target = "high", ignore = true)
    @Mapping(target = "low", ignore = true)
    @Mapping(target = "high52week", ignore = true)
    @Mapping(target = "low52week", ignore = true)
    @Mapping(target = "lastTradedPrice", ignore = true)
    @Mapping(target = "lastTradedPriceDatetime", ignore = true)
    @Mapping(target = "previousClose", ignore = true)
    @Mapping(target = "open", ignore = true)
    @Mapping(target = "close", ignore = true)
    @Mapping(target = "change", ignore = true)
    @Mapping(target = "changePercent", ignore = true)
    @Mapping(target = "volume", ignore = true)
    @Mapping(target = "volume3m", ignore = true)
    fun toDomain(quote: DtoStockQuote?): DomainStockQuote?
    */
}
