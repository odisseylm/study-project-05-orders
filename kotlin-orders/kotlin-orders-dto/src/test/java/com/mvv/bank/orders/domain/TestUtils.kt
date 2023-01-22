package com.mvv.bank.orders.domain


fun amount(amount: String): Amount = Amount.valueOf(amount)

fun currencyPair(currencyPair: String): CurrencyPair = CurrencyPair.valueOf(currencyPair)
