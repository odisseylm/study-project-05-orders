package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL

import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.OrderType as DomainOrderType
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

    // to avoid warnings
    @Mapping(target = "limitPrice", ignore = true)
    @Mapping(target = "stopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    // because earlier it was marked as ignored
    @Mapping(source = "limitPrice", target = "limitPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    // because earlier it was marked as ignored
    @Mapping(source = "stopPrice", target = "stopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun stopOrderToDto(source: DomainStopOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract fun marketOrderToDto(source: DomainMarketOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainAbstractFxCashOrder?): RestFxOrder? =
    //    if (source == null) null else
    //        when (source.orderType) {
    //            DomainOrderType.MARKET_ORDER -> marketOrderToDto(source as DomainFxCashMarketOrder)
    //            DomainOrderType.LIMIT_ORDER  -> limitOrderToDto(source as DomainFxCashLimitOrder)
    //            DomainOrderType.STOP_ORDER   -> stopOrderToDto(source as DomainFxCashStopOrder)
    //        }

    // T O D O: can we do it better without this switch?
    fun toDto(source: DomainOrder?): DtoOrder? {
        val target = DtoOrder()
        return when (source) {
            is DomainMarketOrder -> marketOrderToDto(source, target)
            is DomainLimitOrder  -> limitOrderToDto(source, target)
            is DomainStopOrder   -> stopOrderToDto(source, target)
            else -> null
        }
    }


    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToLimitOrder(source: DtoOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToStopOrder(source: DtoOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    // T O D O: can we do it better without this switch?
    fun toDomain(source: DtoOrder): DomainOrder =
        when (val target = createDomainOrder<DomainOrder>(source)) {
            is DomainMarketOrder -> dtoToMarketOrder(source, target)
            is DomainLimitOrder  -> dtoToLimitOrder(source, target)
            is DomainStopOrder   -> dtoToStopOrder(source, target)
            //else -> null
        }

    override fun chooseOrderTypeClass(orderType: DomainOrderType): KClass<*> = orderType.cashDomainType
}
