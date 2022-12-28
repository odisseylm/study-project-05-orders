package com.mvv.bank.orders.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<JpaFxOrderEntity, Long>
