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

    @Mapping(source = "market.symbol", target = "market")
    @Mapping(source = "resultingRate.currencyPair.base", target = "resultingRateCcy1")
    @Mapping(source = "resultingRate.currencyPair.counter", target = "resultingRateCcy2")
    @Mapping(source = "resultingRate.timestamp", target = "resultingRateTimestamp")
    @Mapping(source = "resultingRate.bid", target = "resultingRateBid")
    @Mapping(source = "resultingRate.ask", target = "resultingRateAsk")
    @Mapping(source = "user.value", target = "user")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
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


    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    @Mapping(target = "resultingRate",  ignore = true)
    // baseOrderAttrsToDomain
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "limitPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToLimitOrder(source: DtoOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    @Mapping(target = "stopPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToStopOrder(source: DtoOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    open fun postInitDomainOrder(source: DtoOrder, @MappingTarget target: DomainOrder) {
        // if (source == null) return

        // T???
        val resultingRateTimestamp = source.resultingRateTimestamp
        if (resultingRateTimestamp != null) {
            val resultingRateCcy1 = source.resultingRateCcy1; val resultingRateCcy2 = source.resultingRateCcy2
            val resultingRateBid  = source.resultingRateBid;  val resultingRateAsk  = source.resultingRateAsk

            checkNotNull(resultingRateCcy1) { "resultingRateCcy1 is null." }
            checkNotNull(resultingRateCcy2) { "resultingRateCcy2 is null." }
            checkNotNull(resultingRateBid)  { "resultingRateBid is null."  }
            checkNotNull(resultingRateAsk)  { "resultingRateAsk is null."  }

            val asLocalDateTime = resultingRateTimestamp.withZoneSameInstant(target.market.zoneId).toLocalDateTime()

            val rate = FxRate(
                market = MarketSymbol.of(source.market),
                timestamp = resultingRateTimestamp,
                marketDate = asLocalDateTime.toLocalDate(),
                marketTime = asLocalDateTime.toLocalTime(),
                currencyPair = CurrencyPair.of(resultingRateCcy1, resultingRateCcy2),
                bid = resultingRateBid,
                ask = resultingRateAsk,
            )

            // there is side effect and resultingQuote/resultingPrice will be set too automatically
            target.resultingRate = rate
        }

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

    //override fun clone(): FxOrderMapper = super.clone() as FxOrderMapper
}
