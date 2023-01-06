package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.conversion.GenericOrderDtoDomainConversion
import com.mvv.bank.orders.domain.of
import org.mapstruct.*
import kotlin.reflect.KClass

import com.mvv.bank.orders.domain.FxRate as DomainFxRate
import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainOrder
import com.mvv.bank.orders.domain.CurrencyPair as DomainCurrencyPair
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainMarketOrder

import com.mvv.bank.orders.repository.jpa.entities.FxOrder as DtoOrder
import com.mvv.bank.orders.repository.jpa.entities.BaseOrder as DtoBaseOrder
import com.mvv.bank.orders.repository.jpa.entities.OrderType as DtoOrderType


@Mapper(componentModel = MAP_STRUCT_COMPONENT_MODEL, config = DomainPrimitiveMappers::class)
@Suppress("CdiInjectionPointsInspection")
abstract class FxOrderMapper : AbstractJpaOrderMapper(),
    GenericOrderDtoDomainConversion<DtoBaseOrder, DtoOrder, DtoOrderType, DomainOrder,DomainMarketOrder, DomainLimitOrder, DomainStopOrder> {

    // Temporary overriding because MapStruct generates method twice in case of diamond inheritance.
    // T O D O: remove it when it is fixed in MapStruct
    abstract override fun orderTypeToDomain(source: DtoOrderType): DomainOrderType

    override fun chooseOrderTypeClass(orderType: DomainOrderType): KClass<*> = orderType.cashDomainType

    // ***************************************************************************************************

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
    // because earlier it was marked as ignored
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract override fun limitOrderToDto(source: DomainLimitOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    // because earlier it was marked as ignored
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract override fun stopOrderToDto(source: DomainStopOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract override fun marketOrderToDto(source: DomainMarketOrder): DtoOrder


    // ***************************************************************************************************

    @Mapping(target = "resultingRate",  expression = "java( mapResultingRate(source) )")
    // product is calculated property
    @Mapping(target = "product", ignore = true)
    // resultingPrice/resultingQuote will be set automatically after setting rate (by side effect)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "limitPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract override fun dtoToLimitOrder(source: DtoOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "stopPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract override fun dtoToStopOrder(source: DtoOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToMarketOrder(source: DtoOrder): DomainMarketOrder

    fun mapResultingRate(dtoOrder: DtoOrder): DomainFxRate? {
        val resultingRateTimestamp = dtoOrder.resultingRateTimestamp
        if (resultingRateTimestamp != null) {
            val resultingRateCcy1 = checkNotNull(dtoOrder.resultingRateCcy1) { "resultingRateCcy1 is null." }
            val resultingRateCcy2 = checkNotNull(dtoOrder.resultingRateCcy2) { "resultingRateCcy2 is null." }
            val resultingRateBid  = checkNotNull(dtoOrder.resultingRateBid)  { "resultingRateBid is null."  }
            val resultingRateAsk  = checkNotNull(dtoOrder.resultingRateAsk)  { "resultingRateAsk is null."  }

            return DomainFxRate.of(marketToDomain(dtoOrder.market)!!, resultingRateTimestamp,
                DomainCurrencyPair.of(resultingRateCcy1, resultingRateCcy2),
                bid = resultingRateBid, ask = resultingRateAsk)
        }
        return null
    }
}
