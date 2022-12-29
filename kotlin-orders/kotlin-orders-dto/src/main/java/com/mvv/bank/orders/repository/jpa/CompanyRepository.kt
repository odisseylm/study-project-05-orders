package com.mvv.bank.orders.repository.jpa

import com.mvv.bank.orders.repository.jpa.entities.Company
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository : JpaRepository<Company, String>
