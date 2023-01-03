package com.mvv.bank.orders.rest.conversion

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import com.mvv.bank.orders.rest.FxRate as RestFxRate
import com.mvv.bank.orders.domain.FxRate as DomainFxRate


@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330")
interface FxRateMapper {
    @Mapping(source = "marketSymbol", target = "market")
    fun toDto(source: DomainFxRate?): RestFxRate?

    // Does not work since 'Ambiguous constructors found for creating'
    //@Mapping(source = "market", target = "marketSymbol")
    //fun toDomain(source: RestFxRate?): DomainFxRate?

    fun toDomain(source: RestFxRate?): DomainFxRate? =
        if (source == null) null else
            DomainFxRate(source.market, source.dateTime, source.marketDate, source.marketTime, source.currencyPair,
                bid = source.bid, ask = source.ask)
}
