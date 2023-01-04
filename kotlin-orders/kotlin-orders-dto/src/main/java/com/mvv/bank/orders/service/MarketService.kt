package com.mvv.bank.orders.service

import com.mvv.bank.orders.domain.Market
import com.mvv.bank.orders.domain.MarketSymbol

interface MarketService {
    fun marketBySymbol(marketSymbol: MarketSymbol): Market
}
