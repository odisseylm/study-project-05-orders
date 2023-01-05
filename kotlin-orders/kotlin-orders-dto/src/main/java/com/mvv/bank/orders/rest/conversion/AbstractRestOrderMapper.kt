package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.AbstractOrderMapper
import com.mvv.bank.util.checkLateInitPropsAreInitialized
import com.mvv.bank.orders.domain.StockOrder as DomainBaseOrder
import com.mvv.bank.orders.rest.entities.BaseOrder as DtoBaseOrder
import com.mvv.bank.orders.rest.entities.OrderType as DomainOrderType
import org.mapstruct.MappingTarget
import org.mapstruct.BeforeMapping


abstract class AbstractRestOrderMapper : AbstractOrderMapper() {

    @BeforeMapping
    @Suppress("UNUSED_PARAMETER")
    fun validateOrderBeforeConvertingToRest(source: DomainBaseOrder, @MappingTarget target: DtoBaseOrder) {
        // We do not perform strict validation to have possibility to see probably wrong order
        // source.validateCurrentState()

        // but we will use simple validation because in any case we will have error/exception
        // but less informative
        checkLateInitPropsAreInitialized(source)
    }


    @BeforeMapping
    open fun validateDtoOrderBeforeLoading(source: DtoBaseOrder, @MappingTarget target: Any) {
        if (source.orderType == DomainOrderType.MARKET_ORDER) {
            require(source.limitPrice == null && source.stopPrice == null) {
                "Market price cannot have limit/stop price (${source.limitPrice.safe}/${source.stopPrice.safe})." }
            require(source.dailyExecutionType == null) {
                "Market price cannot have daily execution type (${source.dailyExecutionType.safe})." }
        }
    }

}
