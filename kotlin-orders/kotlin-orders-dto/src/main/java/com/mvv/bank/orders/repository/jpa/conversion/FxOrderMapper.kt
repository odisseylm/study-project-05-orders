package com.mvv.bank.orders.repository.jpa.conversion

//import com.mvv.bank.orders.domain.FxRate as DomainFxRate
//import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
//import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.repository.jpa.entities.FxOrder
import com.mvv.bank.orders.service.MarketService
import org.mapstruct.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import com.mvv.bank.orders.domain.AbstractFxCashOrder as DomainAbstractFxCashOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainFxCashLimitOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainFxCashMarketOrder
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainFxCashStopOrder
import com.mvv.bank.orders.repository.jpa.entities.FxOrder as JpaFxOrder


@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330", uses = [CurrencyMapper::class])
abstract class FxOrderMapper : Cloneable {
    private lateinit var marketService: MarketService

    @BeforeMapping
    open fun validateOrderBeforeSaving(source: DomainAbstractFxCashOrder, @MappingTarget target: JpaFxOrder) =
        source.validateCurrentState()

    @Mapping(source = "marketSymbol", target = "market")
    @Mapping(source = "resultingRate.currencyPair.base", target = "resultingRateCcy1")
    @Mapping(source = "resultingRate.currencyPair.counter", target = "resultingRateCcy2")
    @Mapping(source = "resultingRate.dateTime", target = "resultingRateDateTime")
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


    @Mapping(source = "market", target = "marketSymbol")
    @Mapping(target = "market",  ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    @Mapping(target = "resultingRate",  ignore = true)
    abstract fun internalDtoToBaseFxCashAttrs(source: JpaFxOrder, @MappingTarget target: DomainAbstractFxCashOrder): DomainAbstractFxCashOrder

    @InheritConfiguration(name = "internalDtoToBaseFxCashAttrs")
    @Mapping(target = "limitPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToLimitOrder(source: JpaFxOrder, @MappingTarget target: DomainFxCashLimitOrder): DomainFxCashLimitOrder

    @InheritConfiguration(name = "internalDtoToBaseFxCashAttrs")
    @Mapping(target = "stopPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToStopOrder(source: JpaFxOrder, @MappingTarget target: DomainFxCashStopOrder): DomainFxCashStopOrder

    @InheritConfiguration(name = "internalDtoToBaseFxCashAttrs")
    abstract fun dtoToMarketOrder(source: JpaFxOrder, @MappingTarget target: DomainFxCashMarketOrder): DomainFxCashMarketOrder

    @AfterMapping
    open fun postInitDomainOrder(source: JpaFxOrder, @MappingTarget target: DomainAbstractFxCashOrder) {
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

        target.validateCurrentState()
    }

    // T O D O: can we do it better without this switch?
    fun toDomain(source: JpaFxOrder): DomainAbstractFxCashOrder {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val target = resolve<DomainAbstractFxCashOrder>(source)
        return when (target) {
            is DomainFxCashMarketOrder -> dtoToMarketOrder(source, target)
            is DomainFxCashLimitOrder  -> dtoToLimitOrder(source, target)
            is DomainFxCashStopOrder   -> dtoToStopOrder(source, target)
            //else -> null
        }
    }


    @ObjectFactory
    fun <T : DomainAbstractFxCashOrder> resolve(source: FxOrder): T {
        val constructor = source.orderType.domainType.primaryConstructor
            ?.apply { if (!isAccessible) isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        return constructor!!.call() as T
    }

    public override fun clone(): FxOrderMapper = super.clone() as FxOrderMapper
}
