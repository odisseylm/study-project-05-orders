package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.domain.StockQuote as DomainStockQuote
import com.mvv.bank.orders.rest.StockQuote as DtoStockQuote
import org.mapstruct.Mapper


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
)
@Suppress("CdiInjectionPointsInspection")
interface StockQuoteMapper {
    fun toDto(quote: DomainStockQuote?): DtoStockQuote?
    fun toDomain(quote: DomainStockQuote?): DtoStockQuote?
}
