package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.domain.*
import org.mapstruct.*
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainStopOrder
import com.mvv.bank.orders.repository.jpa.entities.FxOrder as DtoOrder


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL, config = DomainPrimitiveMappers::class)
@Suppress("CdiInjectionPointsInspection")
abstract class FxOrderMapper : AbstractJpaOrderMapper() {

    @Mapping(source = "resultingRate.bid", target = "resultingRateBid")
    @Mapping(source = "resultingRate.ask", target = "resultingRateAsk")
    @Mapping(source = "resultingRate.timestamp", target = "resultingRateTimestamp")
    @Mapping(source = "resultingRate.currencyPair.base", target = "resultingRateCcy1")
    @Mapping(source = "resultingRate.currencyPair.counter", target = "resultingRateCcy2")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType") // because earlier it was marked as ignored
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType") // because earlier it was marked as ignored
    abstract fun stopOrderToDto(source: DomainStopOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract fun marketOrderToDto(source: DomainMarketOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainOrder?): DtoOrder? =
    //    if (source == null) null else
    //        when (source.orderType) {
    //            DomainOrderType.MARKET_ORDER -> marketOrderToDto(source as DomainMarketOrder)
    //            DomainOrderType.LIMIT_ORDER  -> limitOrderToDto(source as DomainLimitOrder)
    //            DomainOrderType.STOP_ORDER   -> stopOrderToDto(source as DomainStopOrder)
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


    @Mapping(target = "resultingRate",  expression = "java( mapResultingRate(source) )")
    // product is calculated property
    @Mapping(target = "product", ignore = true)
    // resultingPrice/resultingQuote will be set automatically after setting rate (by side effect)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "limitPrice", expression = "java( Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToLimitOrder(source: DtoOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "stopPrice", expression = "java( Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToStopOrder(source: DtoOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    open fun postInitDomainOrder(source: DtoOrder, @MappingTarget target: DomainOrder) =
        target.validateCurrentState()

    fun mapResultingRate(dtoOrder: DtoOrder): FxRate? {
        val resultingRateTimestamp = dtoOrder.resultingRateTimestamp
        if (resultingRateTimestamp != null) {
            val resultingRateCcy1 = checkNotNull(dtoOrder.resultingRateCcy1) { "resultingRateCcy1 is null." }
            val resultingRateCcy2 = checkNotNull(dtoOrder.resultingRateCcy2) { "resultingRateCcy2 is null." }
            val resultingRateBid  = checkNotNull(dtoOrder.resultingRateBid)  { "resultingRateBid is null."  }
            val resultingRateAsk  = checkNotNull(dtoOrder.resultingRateAsk)  { "resultingRateAsk is null."  }

            return FxRate.of(marketToDomain(dtoOrder.market)!!, resultingRateTimestamp,
                CurrencyPair.of(resultingRateCcy1, resultingRateCcy2),
                bid = resultingRateBid, ask = resultingRateAsk)
        }
        return null
    }

    // T O D O: can we do it better without this switch?
    fun toDomain(source: DtoOrder): DomainOrder =
        when (val target = createDomainOrder<DomainOrder>(source)) {
            is DomainMarketOrder -> dtoToMarketOrder(source, target)
            is DomainLimitOrder  -> dtoToLimitOrder(source, target)
            is DomainStopOrder   -> dtoToStopOrder(source, target)
            //else -> null
        }


    @ObjectFactory
    fun <T : DomainOrder> createDomainOrder(source: DtoOrder): T = newOrderInstance(source.orderType.cashDomainType)
}
