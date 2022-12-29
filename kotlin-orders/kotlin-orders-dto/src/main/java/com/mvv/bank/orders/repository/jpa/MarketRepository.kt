package com.mvv.bank.orders.repository.jpa

import com.mvv.bank.orders.repository.jpa.entities.Market
import org.springframework.data.jpa.repository.JpaRepository

interface MarketRepository : JpaRepository<Market, String> {
}