package com.mvv.bank.orders.repository.jpa.conversion

//import com.mvv.bank.orders.domain.FxRate as DomainFxRate
//import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
import org.mapstruct.InheritConfiguration
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainAbstractFxCashOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainFxCashLimitOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainFxCashStopOrder
import com.mvv.bank.orders.repository.jpa.entities.FxOrder as JpaFxOrder
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
    fun toDtoAbstract(source: DomainAbstractFxCashOrder?): JpaFxOrder?

    @InheritConfiguration(name = "toDtoAbstract")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    fun toDto(source: DomainFxCashLimitOrder?): JpaFxOrder?

    @InheritConfiguration(name = "toDtoAbstract")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    fun toDto(source: DomainFxCashStopOrder?): JpaFxOrder?
}
