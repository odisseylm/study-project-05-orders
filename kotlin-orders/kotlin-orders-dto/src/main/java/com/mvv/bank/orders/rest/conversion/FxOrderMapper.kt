package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainStopOrder
import com.mvv.bank.orders.rest.FxOrder as DtoOrder
import org.mapstruct.*


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    uses = [FxRateMapper::class]
)
@Suppress("CdiInjectionPointsInspection")
abstract class FxOrderMapper: AbstractRestOrderMapper() {

    @Mapping(source = "market.symbol", target = "market")
    // to avoid warnings
    @Mapping(target = "limitPrice", ignore = true)
    @Mapping(target = "stopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "stopPrice")
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


    @Mapping(source = "market", target = "marketSymbol")
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "limitPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToLimitOrder(source: DtoOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "stopPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToStopOrder(source: DtoOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    @Suppress("UNUSED_PARAMETER")
    fun postInitDomainOrder(source: DtoOrder, @MappingTarget target: DomainOrder) {
        // if (source == null) return
        target.validateCurrentState()
    }

    // T O D O: can we do it better without this switch?
    fun toDomain(source: DtoOrder): DomainOrder {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val target = resolve<DomainOrder>(source)
        return when (target) {
            is DomainMarketOrder -> dtoToMarketOrder(source, target)
            is DomainLimitOrder  -> dtoToLimitOrder(source, target)
            is DomainStopOrder   -> dtoToStopOrder(source, target)
            //else -> null
        }
    }

    @ObjectFactory
    fun <T : DomainOrder> resolve(source: DtoOrder): T = newOrderInstance(source.orderType.cashDomainType)
}
