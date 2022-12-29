package com.mvv.bank.orders.repository.jpa

import com.mvv.bank.orders.repository.jpa.entities.FxOrder
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<FxOrder, Long>
