package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.AbstractOrderMapper

import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.repository.jpa.entities.OrderType as DtoOrderType
import com.mvv.bank.orders.repository.jpa.entities.BaseOrder as DtoBaseOrder

import org.mapstruct.BeforeMapping
import org.mapstruct.MappingTarget
import org.mapstruct.ObjectFactory

typealias DomainBaseOrder = com.mvv.bank.orders.domain.Order<*,*>


abstract class AbstractJpaOrderMapper : AbstractOrderMapper() {

    abstract fun orderTypeToDomain(orderType: DtoOrderType): DomainOrderType
    abstract fun orderTypeToDto(orderType: DomainOrderType): DtoOrderType

    @BeforeMapping
    open fun validateDomainOrderBeforeConverting(source: DomainBaseOrder, @MappingTarget target: Any) =
        source.validateCurrentState()

    @BeforeMapping
    open fun validateDtoOrderBeforeConverting(source: DtoBaseOrder, @MappingTarget target: Any) {
        if (source.orderType == DtoOrderType.MARKET_ORDER) {
            require(source.limitStopPrice == null) {
                "Market price cannot have limit/stop price (${source.limitStopPrice.safe})." }
            require(source.dailyExecutionType == null) {
                "Market price cannot have daily execution type (${source.dailyExecutionType.safe})." }
        }
    }

    @ObjectFactory
    fun <T : DomainBaseOrder> createDomainOrder(source: DtoBaseOrder): T =
        newOrderInstance(chooseOrderTypeClass(orderTypeToDomain(source.orderType)))
}
