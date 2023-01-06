package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.AbstractOrderMapper
import com.mvv.bank.orders.conversion.DomainBaseOrder
import com.mvv.bank.util.checkLateInitPropsAreInitialized

import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.rest.entities.OrderType as DtoOrderType
import com.mvv.bank.orders.rest.entities.BaseOrder as DtoBaseOrder

import org.mapstruct.MappingTarget
import org.mapstruct.BeforeMapping
import org.mapstruct.ObjectFactory


abstract class AbstractRestOrderMapper : AbstractOrderMapper() {

    // it is for mixin OrderDtoDomainSupport
    fun getOrderType(source: DtoBaseOrder): DtoOrderType = source.orderType
    abstract fun orderTypeToDomain(source: DtoOrderType): DomainOrderType
    // Seems after adding orderTypeToDomain() we also need to add this opposite method
    // otherwise MapStruct uses chooseOrderTypeClass() for mapping DomainOrderType->DtoOrderType. hm...
    abstract fun orderTypeToDto(source: DomainOrderType): DtoOrderType


    @BeforeMapping
    @Suppress("UNUSED_PARAMETER")
    fun validateDomainOrderBeforeConverting(source: DomainBaseOrder, @MappingTarget target: DtoBaseOrder) {
        // We do not perform strict validation to have possibility to see/analyze probably wrong order.
        // source.validateCurrentState()

        // But we will use simple validation because to have better error message.
        // (error/exception will be thrown in any case)
        checkLateInitPropsAreInitialized(source)
    }


    @BeforeMapping
    open fun validateDtoOrderBeforeConverting(source: DtoBaseOrder, @MappingTarget target: Any) {
        if (source.orderType == DtoOrderType.MARKET_ORDER) {
            require(source.limitPrice == null && source.stopPrice == null) {
                "Market price cannot have limit/stop price (${source.limitPrice.safe}/${source.stopPrice.safe})." }
            require(source.dailyExecutionType == null) {
                "Market price cannot have daily execution type (${source.dailyExecutionType.safe})." }
        }
    }

    @ObjectFactory
    fun <T : DomainBaseOrder> createDomainOrder(source: DtoBaseOrder): T =
        newOrderInstance(chooseOrderTypeClass(orderTypeToDomain(source.orderType)))
}
