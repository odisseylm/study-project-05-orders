package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.conversion.GenericOrderDtoDomainConversion

import com.mvv.bank.orders.domain.CashMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.CashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.AbstractCashOrder as DomainOrder
import com.mvv.bank.orders.domain.CashStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.OrderType as DomainOrderType

import com.mvv.bank.orders.rest.entities.OrderType as DtoOrderType
import com.mvv.bank.orders.rest.entities.BaseOrder as DtoBaseOrder
import com.mvv.bank.orders.rest.entities.CashOrder as DtoOrder

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
abstract class CashOrderMapper: AbstractRestOrderMapper(),
    GenericOrderDtoDomainConversion<DtoBaseOrder, DtoOrder, DtoOrderType, DomainOrder, DomainMarketOrder, DomainLimitOrder, DomainStopOrder> {

    // Temporary overriding because MapStruct generates method twice in case of diamond inheritance.
    // T O D O: remove it when it is fixed in MapStruct
    abstract override fun orderTypeToDomain(source: DtoOrderType): DomainOrderType


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
    abstract override fun limitOrderToDto(source: DomainLimitOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    // because earlier it was marked as ignored
    @Mapping(source = "stopPrice", target = "stopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract override fun stopOrderToDto(source: DomainStopOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract override fun marketOrderToDto(source: DomainMarketOrder): DtoOrder


    // ***************************************************************************************************

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToLimitOrder(source: DtoOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToStopOrder(source: DtoOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToMarketOrder(source: DtoOrder): DomainMarketOrder
}
