package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.AbstractOrderMapper
import com.mvv.bank.orders.domain.Order as DomainBaseOrder
import com.mvv.bank.orders.repository.jpa.entities.BaseOrder as DtoBaseOrder
import com.mvv.bank.orders.repository.jpa.entities.OrderType as DomainOrderType
import org.mapstruct.BeforeMapping
import org.mapstruct.MappingTarget


abstract class AbstractJpaOrderMapper : AbstractOrderMapper() {
    @BeforeMapping
    open fun validateDomainOrderBeforeSaving(source: DomainBaseOrder<*, *>, @MappingTarget target: Any) =
        source.validateCurrentState()

    @BeforeMapping
    open fun validateDtoOrderBeforeLoading(source: DtoBaseOrder, @MappingTarget target: Any) {
        if (source.orderType == DomainOrderType.MARKET_ORDER) {
            require(source.limitStopPrice == null) {
                "Market price cannot have limit/stop price (${source.limitStopPrice.safe})." }
            require(source.dailyExecutionType == null) {
                "Market price cannot have daily execution type (${source.dailyExecutionType.safe})." }
        }
    }
}
