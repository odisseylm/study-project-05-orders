package com.mvv.bank.orders.service

import com.mvv.bank.orders.domain.Market

interface MarketService {
    fun marketBySymbol(marketSymbol: String): Market
}
