package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL

import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.OrderType as DomainOrderType

import com.mvv.bank.orders.rest.entities.OrderType as DtoOrderType
import com.mvv.bank.orders.rest.entities.FxOrder as DtoOrder

import org.mapstruct.*
import kotlin.reflect.KClass


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    uses = [
        FxRateMapper::class,
        AmountMapper::class,
    ]
)
@Suppress("CdiInjectionPointsInspection")
abstract class FxOrderMapper: AbstractRestOrderMapper() {

    override fun chooseOrderTypeClass(orderType: DomainOrderType): KClass<*> = orderType.cashDomainType

    // ***************************************************************************************************

    // to avoid warnings
    @Mapping(target = "limitPrice", ignore = true)
    @Mapping(target = "stopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder, @MappingTarget target: DtoOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    // because earlier it was marked as ignored
    @Mapping(source = "limitPrice", target = "limitPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainLimitOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    // because earlier it was marked as ignored
    @Mapping(source = "stopPrice", target = "stopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun stopOrderToDto(source: DomainStopOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract fun marketOrderToDto(source: DomainMarketOrder): DtoOrder


    // ***************************************************************************************************

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToLimitOrder(source: DtoOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToStopOrder(source: DtoOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoOrder): DomainMarketOrder


    // ***************************************************************************************************

    // T O D O: can we do it better without this switch?
    fun toDto(source: DomainOrder): DtoOrder =
        when (source.orderType) {
            DomainOrderType.MARKET_ORDER -> marketOrderToDto(source as DomainMarketOrder)
            DomainOrderType.LIMIT_ORDER  -> limitOrderToDto(source as DomainLimitOrder)
            DomainOrderType.STOP_ORDER   -> stopOrderToDto(source as DomainStopOrder)
        }

    // T O D O: can we do it better without this switch?
    fun toDomain(source: DtoOrder): DomainOrder =
        when (source.orderType) {
            DtoOrderType.MARKET_ORDER -> dtoToMarketOrder(source)
            DtoOrderType.LIMIT_ORDER  -> dtoToLimitOrder(source)
            DtoOrderType.STOP_ORDER   -> dtoToStopOrder(source)
        }
}
