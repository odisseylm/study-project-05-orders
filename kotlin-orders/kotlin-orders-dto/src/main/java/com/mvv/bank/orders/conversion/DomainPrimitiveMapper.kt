package com.mvv.bank.orders.conversion

import com.mvv.bank.orders.domain.*
import org.mapstruct.Mapper
import org.mapstruct.MapperConfig


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL)
interface CurrencyMapper {
    fun toDto(currency: Currency?): String? = currency?.toString()
    fun toDomain(currency: String?): Currency? = if (currency == null) null else Currency.valueOf(currency)
}


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL)
interface UserMapper {
    fun toDto(user: User?): String? = user?.value
    fun toDomain(user: String?): User? = if (user == null) null else User.of(user)
}


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL)
interface EmailMapper {
    fun toDto(email: Email?): String? = email?.value
    fun toDomain(email: String?): Email? = if (email == null) null else Email.of(email)
}


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL)
interface PhoneMapper {
    fun toDto(phone: Phone?): String? = phone?.value
    fun toDomain(phone: String?): Phone? = if (phone == null) null else Phone.of(phone)
}


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL)
interface UserNaturalKeyMapper {
    fun toDto(userNaturalKey: UserNaturalKey?): String? = userNaturalKey?.value
    fun toDomain(userNaturalKey: String?): UserNaturalKey? = if (userNaturalKey == null) null else UserNaturalKey.of(userNaturalKey)
}


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL)
interface MarketSymbolMapper {
    fun toDto(marketSymbol: MarketSymbol?): String? = marketSymbol?.value
    fun toDomain(marketSymbol: String?): MarketSymbol? = if (marketSymbol == null) null else MarketSymbol.of(marketSymbol)
}


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL)
interface CompanySymbolMapper {
    fun toDto(companySymbol: CompanySymbol?): String? = companySymbol?.value
    fun toDomain(companySymbol: String?): CompanySymbol? = if (companySymbol == null) null else CompanySymbol.of(companySymbol)
}


@MapperConfig(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    uses = [
        // Natural key/id mappers
        UserNaturalKeyMapper::class,
        MarketSymbolMapper::class,
        CompanySymbolMapper::class,
        // others
        CurrencyMapper::class,
        EmailMapper::class,
        PhoneMapper::class,
        UserMapper::class,
    ])
interface DomainPrimitiveMappers


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
