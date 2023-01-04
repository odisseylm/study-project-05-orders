package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.CurrencyMapper
import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.service.MarketService
import jakarta.inject.Inject
import org.mapstruct.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

import com.mvv.bank.orders.domain.FxCashStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainOrder

import com.mvv.bank.orders.repository.jpa.entities.FxOrder as DtoOrder
import com.mvv.bank.orders.repository.jpa.entities.OrderType as DtoOrderType


@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330", uses = [CurrencyMapper::class])
@Suppress("CdiInjectionPointsInspection")
abstract class FxOrderMapper : Cloneable {
    @Inject
    private lateinit var marketService: MarketService

    fun map(user: String): User = User.of(user) // TODO: move to base interface
    fun map(user: User): String = user.value    // TODO: move to base interface

    @BeforeMapping
    open fun validateOrderBeforeSaving(source: DomainOrder, @MappingTarget target: DtoOrder) =
        source.validateCurrentState()

    @Mapping(source = "marketSymbol", target = "market")
    @Mapping(source = "resultingRate.currencyPair.base", target = "resultingRateCcy1")
    @Mapping(source = "resultingRate.currencyPair.counter", target = "resultingRateCcy2")
    @Mapping(source = "resultingRate.dateTime", target = "resultingRateDateTime")
    @Mapping(source = "resultingRate.bid", target = "resultingRateBid")
    @Mapping(source = "resultingRate.ask", target = "resultingRateAsk")
    @Mapping(source = "user.value", target = "user")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseCashOrderToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseCashOrderToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseCashOrderToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun stopOrderToDto(source: DomainStopOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseCashOrderToDto")
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


    @Mapping(source = "market", target = "marketSymbol")
    @Mapping(target = "market",  ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    @Mapping(target = "resultingRate",  ignore = true)
    //@Mapping(target = "user", expression = "java( User.of(source.user) )") // TODO: to fix
    abstract fun dtoToBaseCashOrder(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "dtoToBaseCashOrder")
    @Mapping(target = "limitPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToLimitOrder(source: DtoOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "dtoToBaseCashOrder")
    @Mapping(target = "stopPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToStopOrder(source: DtoOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "dtoToBaseCashOrder")
    abstract fun dtoToMarketOrder(source: DtoOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    open fun postInitDomainOrder(source: DtoOrder, @MappingTarget target: DomainOrder) {
        // if (source == null) return
        target.market = marketService.marketBySymbol(source.market)

        val resultingRateDateTime = source.resultingRateDateTime
        if (resultingRateDateTime != null) {
            val resultingRateCcy1 = source.resultingRateCcy1; val resultingRateCcy2 = source.resultingRateCcy2
            val resultingRateBid  = source.resultingRateBid;  val resultingRateAsk  = source.resultingRateAsk

            checkNotNull(resultingRateCcy1) { "resultingRateCcy1 is null." }
            checkNotNull(resultingRateCcy2) { "resultingRateCcy2 is null." }
            checkNotNull(resultingRateBid)  { "resultingRateBid is null."  }
            checkNotNull(resultingRateAsk)  { "resultingRateAsk is null."  }

            val asLocalDateTime = resultingRateDateTime.withZoneSameInstant(target.market.zoneId).toLocalDateTime()

            val rate = FxRate(
                marketSymbol = source.market,
                dateTime = resultingRateDateTime,
                marketDate = asLocalDateTime.toLocalDate(),
                marketTime = asLocalDateTime.toLocalTime(),
                currencyPair = CurrencyPair.of(resultingRateCcy1, resultingRateCcy2),
                bid = resultingRateBid,
                ask = resultingRateAsk,
            )

            // there is side effect and resultingQuote/resultingPrice will be set too automatically
            target.resultingRate = rate
        }

        if (source.orderType == DtoOrderType.MARKET_ORDER) {
            require(source.limitStopPrice == null) {
                "Market price cannot have limit/stop price (${source.limitStopPrice.safe})." }
            require(source.dailyExecutionType == null) {
                "Market price cannot have daily execution type (${source.dailyExecutionType.safe})." }
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
    fun <T : DomainOrder> resolve(source: DtoOrder): T {
        val constructor = source.orderType.cashDomainType.primaryConstructor
            ?.apply { if (!isAccessible) isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        return constructor!!.call() as T
    }

    public override fun clone(): FxOrderMapper = super.clone() as FxOrderMapper
}
