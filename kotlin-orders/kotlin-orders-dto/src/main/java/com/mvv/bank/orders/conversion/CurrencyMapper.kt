package com.mvv.bank.orders.conversion

import com.mvv.bank.orders.domain.Currency
import org.mapstruct.Mapper

@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330")
interface CurrencyMapper {

    fun toDto(currency: Currency?): String? = currency?.toString()

    fun fromDto(currency: String?): Currency? = if (currency == null) null else Currency.valueOf(currency)
}

/*
// Now mapstruct mappers are written in java because just now 'kapt' does not work under jdk17
@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    default String toDto(Currency currency) {
        return (currency == null) ? null : currency.getValue();
    }

    default Currency fromDto(String currency) {
        return (currency == null) ? null : Currency.valueOf(currency);
    }
}
*/
