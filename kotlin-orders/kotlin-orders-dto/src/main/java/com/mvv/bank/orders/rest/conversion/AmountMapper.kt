package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.CurrencyMapper
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import org.mapstruct.Mapper
import com.mvv.bank.orders.domain.Amount as DomainAmount
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.rest.entities.Amount as DtoAmount

@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL, uses = [CurrencyMapper::class])
interface AmountMapper {
    fun toDto(source: DomainAmount?): DtoAmount?
    fun toDomain(source: DtoAmount?): DomainAmount? = if (source == null) null
        else DomainAmount.of(source.value, DomainCurrency.of(source.currency))
}
