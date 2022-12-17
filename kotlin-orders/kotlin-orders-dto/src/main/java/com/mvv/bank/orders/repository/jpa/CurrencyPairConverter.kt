package com.mvv.bank.orders.repository.jpa

import com.mvv.bank.orders.domain.CurrencyPair
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class CurrencyPairJpaConverter : AttributeConverter<CurrencyPair?, String?> {
    override fun convertToDatabaseColumn(currencyPair: CurrencyPair?): String? = currencyPair?.toString()

    override fun convertToEntityAttribute(currencyPair: String?): CurrencyPair? =
        if (currencyPair == null) null else CurrencyPair.valueOf(currencyPair)
}
