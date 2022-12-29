package com.mvv.bank.orders.repository.jpa.entities

import com.mvv.bank.jpa.AttributeConverterAdapter
import com.mvv.bank.orders.domain.Currency
import com.mvv.bank.orders.domain.CurrencyPair
import jakarta.persistence.Converter


@Converter(autoApply = true)
class CurrencyConverter : AttributeConverterAdapter<Currency, String> (
    valueToSqlText = { it.toString() },
    sqlTextToEnum = { Currency.valueOf(it) },
)


@Converter(autoApply = true)
class CurrencyPairConverter : AttributeConverterAdapter<CurrencyPair, String> (
    valueToSqlText = { it.toString() },
    sqlTextToEnum = { CurrencyPair.valueOf(it) },
)
