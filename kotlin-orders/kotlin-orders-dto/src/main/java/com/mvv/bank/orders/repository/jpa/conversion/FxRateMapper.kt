package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.conversion.CurrencyMapper
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.domain.CurrencyPair
import com.mvv.bank.orders.domain.FxRate as DomainFxRate
import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
import org.mapstruct.Mapper
import org.mapstruct.Mapping


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL, uses = [CurrencyMapper::class])
interface FxRateMapper {
    //@Mapping(target = "currencyPair", expression = "java(com.mvv.bank.orders.domain.CurrencyPair.of(source.cur1, source.cur2))")
    //@Mapping(target = "currencyPair", expression = "java(com.mvv.bank.orders.domain.CurrencyPair.of(source.getCur1(), source.getCur2()))")
    fun fromDto(source: JpaFxRate?): DomainFxRate? =
        if (source == null) null else DomainFxRate(
            source.market, source.timestamp, source.marketDate, source.marketTime,
            CurrencyPair.of(source.cur1, source.cur2), bid = source.bid, ask = source.ask)

    @Mapping(source = "currencyPair.base", target = "cur1")
    @Mapping(source = "currencyPair.counter", target = "cur2")
    fun toDto(source: DomainFxRate?): JpaFxRate?
}
