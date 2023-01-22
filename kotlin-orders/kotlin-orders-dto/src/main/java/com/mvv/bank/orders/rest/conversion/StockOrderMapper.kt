package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.conversion.GenericOrderDtoDomainConversion
import org.mapstruct.*
import kotlin.reflect.KClass

import com.mvv.bank.orders.rest.entities.StockOrder as DtoOrder
import com.mvv.bank.orders.rest.entities.BaseOrder as DtoBaseOrder
import com.mvv.bank.orders.rest.entities.OrderType as DtoOrderType

import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.StockStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.AbstractStockOrder as DomainOrder
import com.mvv.bank.orders.domain.StockLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.StockMarketOrder as DomainMarketOrder


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    uses = [
        StockQuoteMapper::class,
        AmountMapper::class,
    ]
)
@Suppress("CdiInjectionPointsInspection")
abstract class StockOrderMapper: AbstractRestOrderMapper(),
    GenericOrderDtoDomainConversion<DtoBaseOrder, DtoOrder, DtoOrderType, DomainOrder, DomainMarketOrder, DomainLimitOrder, DomainStopOrder> {

    // Temporary overriding because MapStruct generates method twice in case of diamond inheritance.
    // T O D O: remove it when it is fixed in MapStruct
    abstract override fun orderTypeToDomain(source: DtoOrderType): DomainOrderType

    override fun chooseOrderTypeClass(orderType: DomainOrderType): KClass<*> = orderType.stockDomainType

    // ***************************************************************************************************

    // to avoid warnings
    @Mapping(target = "limitPrice", ignore = true)
    @Mapping(target = "stopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

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

    @Mapping(source = "product", target = "company")
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToLimitOrder(source: DtoOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToStopOrder(source: DtoOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToMarketOrder(source: DtoOrder): DomainMarketOrder
}
