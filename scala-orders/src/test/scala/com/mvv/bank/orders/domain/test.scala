package com.mvv.bank.orders.domain


def amount(amount: String): Amount = Amount.valueOf(amount)

def currencyPair(currencyPair: String): CurrencyPair = CurrencyPair.valueOf(currencyPair)
