package com.mvv.bank.orders.conversion

import com.mvv.bank.orders.domain.CompanyProvider
import com.mvv.bank.orders.domain.CompanySymbol
import com.mvv.bank.orders.domain.MarketProvider
import com.mvv.bank.orders.domain.MarketSymbol

import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.Company as DomainCompany
import com.mvv.bank.orders.domain.Market as DomainMarket

import com.mvv.bank.util.internalNewInstance
import jakarta.inject.Inject
import org.mapstruct.AfterMapping
import org.mapstruct.MappingTarget
import kotlin.reflect.KClass

typealias DomainBaseOrder = com.mvv.bank.orders.domain.Order<*,*>


@Suppress("CdiInjectionPointsInspection", "MemberVisibilityCanBePrivate")
abstract class AbstractOrderMapper: Cloneable {
    @Inject
    protected lateinit var marketProvider: MarketProvider
    @Inject
    protected lateinit var companyProvider: CompanyProvider

    // Ideally it should be put into separate MarketMapper but easy pure unit testing it is there now
    // (to avoid injection sub-dependencies into dependencies)
    fun marketToDto(market: DomainMarket?): String? = market?.symbol?.value
    fun marketToDomain(marketSymbol: String?): DomainMarket? =
        if (marketSymbol == null) null else marketToDomain(MarketSymbol(marketSymbol))
    fun marketToDomain(marketSymbol: MarketSymbol?): DomainMarket? =
        if (marketSymbol == null) null else marketProvider.marketBySymbol(marketSymbol)

    // Ideally it should be put into separate MarketMapper but easy pure unit testing it is there now
    // (to avoid injection sub-dependencies into dependencies)
    fun companyToDto(company: DomainCompany?): String? = company?.symbol?.value
    fun companyToDomain(companySymbol: String?): DomainCompany? =
        if (companySymbol == null) null else companyToDomain(CompanySymbol(companySymbol))
    fun companyToDomain(companySymbol: CompanySymbol?): DomainCompany? =
        if (companySymbol == null) null else companyProvider.companyBySymbol(companySymbol)


    @Suppress("UNCHECKED_CAST")
    protected fun <T> newOrderInstance(type: KClass<*>): T = internalNewInstance(type) as T

    @AfterMapping
    open fun validateDomainOrderAfterCreation(source: Any, @MappingTarget target: DomainBaseOrder) =
        target.validateCurrentState()

    // It is designed to choose cash or stock order.
    abstract fun chooseOrderTypeClass(orderType: DomainOrderType): KClass<*>

    // for easy testing
    public override fun clone(): AbstractOrderMapper = super.clone() as AbstractOrderMapper
}
