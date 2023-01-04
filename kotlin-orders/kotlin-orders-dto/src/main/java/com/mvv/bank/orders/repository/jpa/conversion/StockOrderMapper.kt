package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import com.mvv.bank.orders.domain.*
import org.mapstruct.*
import com.mvv.bank.orders.domain.StockLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.StockMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.StockOrder as DomainOrder
import com.mvv.bank.orders.domain.StockStopOrder as DomainStopOrder
import com.mvv.bank.orders.repository.jpa.entities.StockOrder as DtoOrder
import com.mvv.bank.orders.repository.jpa.entities.StockOrder as DtoStockOrder


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    imports = [Currency::class, Amount::class],
)
@Suppress("CdiInjectionPointsInspection")
abstract class StockOrderMapper : AbstractJpaOrderMapper() {

    @Mapping(source = "market.symbol", target = "market")
    @Mapping(source = "resultingPrice.value", target = "resultingPrice")
    @Mapping(source = "resultingQuote.bid.value", target = "resultingQuoteBid")
    @Mapping(source = "resultingQuote.ask.value", target = "resultingQuoteAsk")
    @Mapping(source = "resultingQuote.timestamp", target = "resultingQuoteTimestamp")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "priceCurrency", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoStockOrder?): DtoStockOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "limitPrice.currency", target = "priceCurrency")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoStockOrder?): DtoStockOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "stopPrice.currency", target = "priceCurrency")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun stopOrderToDto(source: DomainStopOrder?, @MappingTarget target: DtoStockOrder?): DtoStockOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract fun marketOrderToDto(source: DomainMarketOrder?, @MappingTarget target: DtoStockOrder?): DtoStockOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainAbstractFxCashOrder?): JpaFxOrder? =
    //    if (source == null) null else
    //        when (source.orderType) {
    //            DomainOrderType.MARKET_ORDER -> marketOrderToDto(source as DomainFxCashMarketOrder)
    //            DomainOrderType.LIMIT_ORDER  -> limitOrderToDto(source as DomainFxCashLimitOrder)
    //            DomainOrderType.STOP_ORDER   -> stopOrderToDto(source as DomainFxCashStopOrder)
    //        }

    // T O D O: can we do it better without this switch?
    fun toDto(source: DomainOrder?): DtoStockOrder? {
        val target = DtoStockOrder()
        return when (source) {
            is DomainMarketOrder -> marketOrderToDto(source, target)
            is DomainLimitOrder  -> limitOrderToDto(source, target)
            is DomainStopOrder   -> stopOrderToDto(source, target)
            else -> null
        }
    }


    @Mapping(source = "product", target = "company")
    @Mapping(target = "resultingPrice", expression = "java( ( source.getResultingPrice() == null ? null : Amount.of(source.getResultingPrice(), Currency.of(source.getPriceCurrency())) ) )")
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun baseOrderAttrsToDomain(source: DtoStockOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "limitPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract fun dtoToLimitOrder(source: DtoStockOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "stopPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract fun dtoToStopOrder(source: DtoStockOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoStockOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    open fun postInitDomainOrder(source: DtoStockOrder, @MappingTarget target: DomainOrder) {
        // if (source == null) return

        if (source.resultingQuoteTimestamp != null) {
            target.resultingQuote = StockQuote.of(
                target.market, target.company, source.resultingQuoteTimestamp!!,
                bid = source.resultingQuoteBid!!, ask = source.resultingQuoteAsk!!, Currency.of(source.priceCurrency),
            )
        }

        target.validateCurrentState()
    }

    // T O D O: can we do it better without this switch?
    fun toDomain(source: DtoStockOrder): DomainOrder {
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
    fun <T : DomainOrder> resolve(source: DtoOrder): T = newOrderInstance(source.orderType.stockDomainType)

    //override fun clone(): StockOrderMapper = super.clone() as StockOrderMapper
}
