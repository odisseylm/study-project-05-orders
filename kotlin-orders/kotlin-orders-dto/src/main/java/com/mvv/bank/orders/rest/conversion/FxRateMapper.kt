package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import org.mapstruct.Mapper
import com.mvv.bank.orders.rest.entities.FxRate as RestFxRate
import com.mvv.bank.orders.domain.FxRate as DomainFxRate


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL, config = DomainPrimitiveMappers::class)
interface FxRateMapper {
    fun toDto(source: DomainFxRate?): RestFxRate?

    // Does not work since 'Ambiguous constructors found for creating'
    fun toDomain(source: RestFxRate?): DomainFxRate?

    //fun toDomain(source: RestFxRate?): DomainFxRate? =
    //    if (source == null) null else
    //        DomainFxRate(source.market, source.dateTime, source.marketDate, source.marketTime, source.currencyPair,
    //            bid = source.bid, ask = source.ask)
}
