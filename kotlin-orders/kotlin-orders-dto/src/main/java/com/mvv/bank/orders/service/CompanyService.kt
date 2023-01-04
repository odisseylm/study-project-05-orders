package com.mvv.bank.orders.service

import com.mvv.bank.orders.domain.Company

interface CompanyService {
    fun companyBySymbol(companySymbol: String): Company
}
