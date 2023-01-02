package com.mvv.bank.orders.repository.jpa.conversion

//import com.mvv.bank.orders.domain.FxRate as DomainFxRate
//import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
//import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainAbstractFxCashOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainFxCashLimitOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainFxCashStopOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainFxCashMarketOrder
import com.mvv.bank.orders.repository.jpa.entities.FxOrder as JpaFxOrder
import org.mapstruct.InheritConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.SubclassMapping


@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330", uses = [CurrencyMapper::class])
interface FxOrderMapper {
    //@Mapping(target = "currencyPair", expression = "java(com.mvv.bank.orders.domain.CurrencyPair.of(source.cur1, source.cur2))")
    //fun fromDto(source: JpaFxOrder?): DomainAbstractFxCashOrder?

    //@Mapping(source = "currencyPair.base", target = "cur1")
    //@Mapping(source = "currencyPair.counter", target = "cur2")

    //@Named("commonFxCashOrderToDto")
    @SubclassMapping(source = DomainAbstractFxCashOrder::class, target = JpaFxOrder::class)
    @Mapping(source = "marketSymbol", target = "market")
    @Mapping(source = "resultingRate.currencyPair.base", target = "resultingRateCcy1")
    @Mapping(source = "resultingRate.currencyPair.counter", target = "resultingRateCcy2")
    @Mapping(source = "resultingRate.bid", target = "resultingRateBid")
    @Mapping(source = "resultingRate.ask", target = "resultingRateAsk")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    fun internalBaseFxCashAttrsToDto(source: DomainAbstractFxCashOrder?): JpaFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    fun limitOrderToDto(source: DomainFxCashLimitOrder?): JpaFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    fun stopOrderToDto(source: DomainFxCashStopOrder?): JpaFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    fun marketOrderToDto(source: DomainFxCashMarketOrder?): JpaFxOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainAbstractFxCashOrder?): JpaFxOrder? =
    //    if (source == null) null else
    //        when (source.orderType) {
    //            DomainOrderType.MARKET_ORDER -> marketOrderToDto(source as DomainFxCashMarketOrder)
    //            DomainOrderType.LIMIT_ORDER  -> limitOrderToDto(source as DomainFxCashLimitOrder)
    //            DomainOrderType.STOP_ORDER   -> stopOrderToDto(source as DomainFxCashStopOrder)
    //        }

    // T O D O: can we do it better without this switch?
    fun toDto(source: DomainAbstractFxCashOrder?): JpaFxOrder? =
        when (source) {
            is DomainFxCashMarketOrder -> marketOrderToDto(source)
            is DomainFxCashLimitOrder  -> limitOrderToDto(source)
            is DomainFxCashStopOrder   -> stopOrderToDto(source)
            else -> null
        }
}
