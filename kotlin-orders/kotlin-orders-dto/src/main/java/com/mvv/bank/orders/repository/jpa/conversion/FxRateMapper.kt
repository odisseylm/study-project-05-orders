package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.domain.FxRate as DomainFxRate
import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
import org.mapstruct.Mapper
import org.mapstruct.Mapping


@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330", uses = [CurrencyMapper::class])
interface FxRateMapper {
    @Mapping(target = "currencyPair", expression = "java(com.mvv.bank.orders.domain.CurrencyPair.of(source.cur1, source.cur2))")
    //@Mapping(target = "currencyPair", expression = "java(com.mvv.bank.orders.domain.CurrencyPair.of(source.getCur1(), source.getCur2()))")
    fun fromDto(source: JpaFxRate?): DomainFxRate?

    @Mapping(source = "currencyPair.base", target = "cur1")
    @Mapping(source = "currencyPair.counter", target = "cur2")
    fun toDto(source: DomainFxRate?): JpaFxRate?
}
