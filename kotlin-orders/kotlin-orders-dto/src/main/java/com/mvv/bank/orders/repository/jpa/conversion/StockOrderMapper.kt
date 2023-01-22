package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.conversion.GenericOrderDtoDomainConversion
import com.mvv.bank.orders.domain.of
import org.mapstruct.*
import kotlin.reflect.KClass

import com.mvv.bank.orders.domain.AbstractStockOrder as DomainOrder
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.StockQuote as DomainStockQuote
import com.mvv.bank.orders.domain.StockStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.StockLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.StockMarketOrder as DomainMarketOrder

import com.mvv.bank.orders.repository.jpa.entities.StockOrder as DtoOrder
import com.mvv.bank.orders.repository.jpa.entities.BaseOrder as DtoBaseOrder
import com.mvv.bank.orders.repository.jpa.entities.OrderType as DtoOrderType


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    //imports = [Currency::class, Amount::class],
)
@Suppress("CdiInjectionPointsInspection")
abstract class StockOrderMapper : AbstractJpaOrderMapper(),
    GenericOrderDtoDomainConversion<DtoBaseOrder, DtoOrder, DtoOrderType, DomainOrder, DomainMarketOrder, DomainLimitOrder, DomainStopOrder> {

    // Temporary overriding because MapStruct generates method twice in case of diamond inheritance.
    // T O D O: remove it when it is fixed in MapStruct
    abstract override fun orderTypeToDomain(source: DtoOrderType): DomainOrderType

    override fun chooseOrderTypeClass(orderType: DomainOrderType): KClass<*> = orderType.stockDomainType

    // ***************************************************************************************************

    @Mapping(source = "resultingPrice.value", target = "resultingPrice")
    @Mapping(source = "resultingQuote.bid.value", target = "resultingQuoteBid")
    @Mapping(source = "resultingQuote.ask.value", target = "resultingQuoteAsk")
    @Mapping(source = "resultingQuote.timestamp", target = "resultingQuoteTimestamp")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "priceCurrency", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "limitPrice.currency", target = "priceCurrency")
    // because earlier it was marked as ignored
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract override fun limitOrderToDto(source: DomainLimitOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "stopPrice.currency", target = "priceCurrency")
    // because earlier it was marked as ignored
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract override fun stopOrderToDto(source: DomainStopOrder): DtoOrder

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract override fun marketOrderToDto(source: DomainMarketOrder): DtoOrder


    // ***************************************************************************************************

    @Mapping(source = "product", target = "company")
    // to avoid warnings
    @Mapping(target = "resultingPrice", expression = "java( Amount.of(source.getResultingPrice(), Currency.of(source.getPriceCurrency())) )")
    @Mapping(target = "resultingQuote", expression = "java( mapResultingQuote(source) )")
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "limitPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract override fun dtoToLimitOrder(source: DtoOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "stopPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract override fun dtoToStopOrder(source: DtoOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract override fun dtoToMarketOrder(source: DtoOrder): DomainMarketOrder

    fun mapResultingQuote(dtoOrder: DtoOrder): DomainStockQuote? {
        val resultingQuoteTimestamp = dtoOrder.resultingQuoteTimestamp
        return if (resultingQuoteTimestamp == null) null
        else {
            val resultingQuoteBid = checkNotNull(dtoOrder.resultingQuoteBid) { "resultingQuoteBid is null." }
            val resultingQuoteAsk = checkNotNull(dtoOrder.resultingQuoteAsk) { "resultingQuoteAsk is null." }

            DomainStockQuote.of(
                marketToDomain(dtoOrder.market)!!, companyToDomain(dtoOrder.product)!!,
                resultingQuoteTimestamp,
                bid = resultingQuoteBid, ask = resultingQuoteAsk,
                DomainCurrency.of(dtoOrder.priceCurrency)
            )
        }
    }

}
