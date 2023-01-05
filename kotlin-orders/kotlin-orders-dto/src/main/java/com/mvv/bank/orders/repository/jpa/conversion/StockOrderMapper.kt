package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.domain.*
import org.mapstruct.*
import com.mvv.bank.orders.domain.StockQuote as DomainStockQuote
import com.mvv.bank.orders.domain.StockLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.StockMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.StockOrder as DomainOrder
import com.mvv.bank.orders.domain.StockStopOrder as DomainStopOrder
import com.mvv.bank.orders.repository.jpa.entities.StockOrder as DtoOrder


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    imports = [Currency::class, Amount::class],
)
@Suppress("CdiInjectionPointsInspection")
abstract class StockOrderMapper : AbstractJpaOrderMapper() {

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
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType") // because earlier it was marked as ignored
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "stopPrice.currency", target = "priceCurrency")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType") // because earlier it was marked as ignored
    abstract fun stopOrderToDto(source: DomainStopOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract fun marketOrderToDto(source: DomainMarketOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainAbstractFxCashOrder?): JpaFxOrder? =
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


    @Mapping(source = "product", target = "company")
    // to avoid warnings
    @Mapping(target = "resultingPrice", expression = "java( Amount.of(source.getResultingPrice(), Currency.of(source.getPriceCurrency())) )")
    @Mapping(target = "resultingQuote", expression = "java( mapResultingQuote(source) )")
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "limitPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract fun dtoToLimitOrder(source: DtoOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "stopPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract fun dtoToStopOrder(source: DtoOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    open fun postInitDomainOrder(source: DtoOrder, @MappingTarget target: DomainOrder) =
        target.validateCurrentState()

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
                Currency.of(dtoOrder.priceCurrency)
            )
        }
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
    fun <T : DomainOrder> createDomainOrder(source: DtoOrder): T = newOrderInstance(source.orderType.stockDomainType)
}
