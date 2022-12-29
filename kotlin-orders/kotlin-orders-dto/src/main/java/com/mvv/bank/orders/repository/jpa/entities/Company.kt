package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "COMPANIES")
@Suppress("JpaDataSourceORMInspection")
class Company {

    @Id
    //@NaturalId
    @Column(name = "SYMBOL", nullable = false)
    lateinit var symbol: String // see https://stockanalysis.com/stocks/  https://www.investopedia.com/terms/s/stocksymbol.asp

    @Column(name = "NAME", nullable = false)
    lateinit var name: String

    @Column(name = "ISIN", nullable = false)
    lateinit var isin: String
}
