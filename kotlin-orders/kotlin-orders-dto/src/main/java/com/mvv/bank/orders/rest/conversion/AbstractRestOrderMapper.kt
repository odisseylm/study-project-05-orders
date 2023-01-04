package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.AbstractOrderMapper
import com.mvv.bank.orders.rest.BaseOrder as DtoBaseOrder
import com.mvv.bank.orders.rest.OrderType as DomainOrderType
import org.mapstruct.MappingTarget
import org.mapstruct.BeforeMapping


abstract class AbstractRestOrderMapper : AbstractOrderMapper() {

    // I guess it is not needed or even small evil ))
    //@BeforeMapping
    //fun validateOrderBeforeConvertingToRest(source: AbstractFxCashOrder, @MappingTarget target: RestFxOrder) =
    //    source.validateCurrentState()


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
