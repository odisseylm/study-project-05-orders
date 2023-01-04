package com.mvv.bank.orders.service

import com.mvv.bank.orders.domain.Company
import com.mvv.bank.orders.domain.CompanySymbol

interface CompanyService {
    fun companyBySymbol(companySymbol: CompanySymbol): Company
}
