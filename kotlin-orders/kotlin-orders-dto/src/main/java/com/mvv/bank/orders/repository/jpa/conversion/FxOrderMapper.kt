package com.mvv.bank.orders.repository.jpa.conversion

//import com.mvv.bank.orders.domain.FxRate as DomainFxRate
//import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
//import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.Amount
import com.mvv.bank.orders.repository.jpa.entities.FxOrder
import com.mvv.bank.orders.service.MarketService
import org.mapstruct.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainAbstractFxCashOrder
import com.mvv.bank.orders.domain.DailyExecutionType as DomainDailyExecutionType
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainFxCashLimitOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainFxCashMarketOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainFxCashStopOrder
import com.mvv.bank.orders.repository.jpa.entities.FxOrder as JpaFxOrder


@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330", uses = [CurrencyMapper::class])
abstract class FxOrderMapper {
    private lateinit var marketService: MarketService

    @Mapping(source = "marketSymbol", target = "market")
    @Mapping(source = "resultingRate.currencyPair.base", target = "resultingRateCcy1")
    @Mapping(source = "resultingRate.currencyPair.counter", target = "resultingRateCcy2")
    @Mapping(source = "resultingRate.bid", target = "resultingRateBid")
    @Mapping(source = "resultingRate.ask", target = "resultingRateAsk")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun internalBaseFxCashAttrsToDto(source: DomainAbstractFxCashOrder?, @MappingTarget target: JpaFxOrder?): JpaFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainFxCashLimitOrder?, @MappingTarget target: JpaFxOrder?): JpaFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun stopOrderToDto(source: DomainFxCashStopOrder?, @MappingTarget target: JpaFxOrder?): JpaFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    abstract fun marketOrderToDto(source: DomainFxCashMarketOrder?, @MappingTarget target: JpaFxOrder?): JpaFxOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainAbstractFxCashOrder?): JpaFxOrder? =
    //    if (source == null) null else
    //        when (source.orderType) {
    //            DomainOrderType.MARKET_ORDER -> marketOrderToDto(source as DomainFxCashMarketOrder)
    //            DomainOrderType.LIMIT_ORDER  -> limitOrderToDto(source as DomainFxCashLimitOrder)
    //            DomainOrderType.STOP_ORDER   -> stopOrderToDto(source as DomainFxCashStopOrder)
    //        }

    // T O D O: can we do it better without this switch?
    fun toDto(source: DomainAbstractFxCashOrder?): JpaFxOrder? {
        val target = JpaFxOrder()
        return when (source) {
            is DomainFxCashMarketOrder -> marketOrderToDto(source, target)
            is DomainFxCashLimitOrder  -> limitOrderToDto(source, target)
            is DomainFxCashStopOrder   -> stopOrderToDto(source, target)
            else -> null
        }
    }

    @InheritConfiguration(name = "updateFxCashOrder")
    @Mapping(source = "market", target = "marketSymbol")
    @Mapping(target = "market", ignore = true)
    //@Mapping(source = "resultingRate.currencyPair.base", target = "resultingRateCcy1")
    //@Mapping(source = "resultingRate.currencyPair.counter", target = "resultingRateCcy2")
    //@Mapping(source = "resultingRate.bid", target = "resultingRateBid")
    //@Mapping(source = "resultingRate.ask", target = "resultingRateAsk")
    // to avoid warnings // TODO: temp
    //@Mapping(target = "limitPrice", ignore = true)
    //@Mapping(target = "stopPrice", ignore = true)
    //@Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun toDomain(jpaOrder: FxOrder?): DomainAbstractFxCashOrder?

    @AfterMapping
    open fun postInitDomainOrder(source: JpaFxOrder, @MappingTarget target: DomainAbstractFxCashOrder) {
        // if (source == null) return
        target.market = marketService.marketBySymbol(source.market)

        // TODO: how to do it without IFs
        if (target is DomainFxCashLimitOrder) {
            target.limitPrice = Amount.of(source.limitStopPrice!!, target.priceCurrency)
            target.dailyExecutionType = DomainDailyExecutionType.valueOf(source.dailyExecutionType!!.name)
        }
    }

    //@AfterMapping
    //open fun postInitDomainOrder22(source: JpaFxOrder, @MappingTarget target: DomainFxCashLimitOrder) {
    //    // if (source == null) return
    //    target.limitPrice = Amount.of(source.limitStopPrice!!, target.priceCurrency)
    //    target.dailyExecutionType = DomainDailyExecutionType.valueOf(source.dailyExecutionType!!.name)
    //}

    @ObjectFactory
    fun <T : DomainAbstractFxCashOrder?> resolve(source: FxOrder): T { //, @TargetType type: Class<T>): T {
        val constructor = source.orderType.domainType.primaryConstructor
            ?.apply { if (!isAccessible) isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        return constructor!!.call() as T
    }
}
