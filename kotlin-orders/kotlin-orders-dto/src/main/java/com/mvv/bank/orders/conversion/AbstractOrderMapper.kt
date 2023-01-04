package com.mvv.bank.orders.conversion

import com.mvv.bank.orders.domain.Company as DomainCompany
import com.mvv.bank.orders.domain.Market as DomainMarket
import com.mvv.bank.orders.service.CompanyService
import com.mvv.bank.orders.service.MarketService
import jakarta.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible


@Suppress("CdiInjectionPointsInspection")
abstract class AbstractOrderMapper: Cloneable {
    @Inject
    protected lateinit var marketService: MarketService
    @Inject
    protected lateinit var companyService: CompanyService

    // Ideally it should be put into separate MarketMapper but easy pure unit testing it is there now
    // (to avoid injection sub-dependencies into dependencies)
    fun marketToDto(market: DomainMarket?): String? = market?.symbol
    fun marketToDomain(marketSymbol: String?): DomainMarket? =
        if (marketSymbol == null) null else marketService.marketBySymbol(marketSymbol)

    // Ideally it should be put into separate MarketMapper but easy pure unit testing it is there now
    // (to avoid injection sub-dependencies into dependencies)
    fun companyToDto(company: DomainCompany?): String? = company?.symbol
    fun companyToDomain(companySymbol: String?): DomainCompany? =
        if (companySymbol == null) null else companyService.companyBySymbol(companySymbol)

    protected fun <T> newOrderInstance(type: KClass<*>): T {
        val constructor = type.primaryConstructor
            ?.apply { if (!isAccessible) isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        return constructor!!.call() as T
    }


    // for easy testing
    public override fun clone(): AbstractOrderMapper = super.clone() as AbstractOrderMapper
}
