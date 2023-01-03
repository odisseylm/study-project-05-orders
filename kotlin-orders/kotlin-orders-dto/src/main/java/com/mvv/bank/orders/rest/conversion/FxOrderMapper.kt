package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.CurrencyMapper
import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.rest.FxOrder
import com.mvv.bank.orders.service.MarketService
import jakarta.inject.Inject
import com.mvv.bank.orders.rest.FxOrder as DtoFxOrder
import com.mvv.bank.orders.rest.OrderType as DtoOrderType
import org.mapstruct.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible


@Mapper(componentModel = "spring, default, cdi, jakarta, jsr330", uses = [CurrencyMapper::class, FxRateMapper::class])
abstract class FxOrderMapper: Cloneable {
    @Inject
    private lateinit var marketService: MarketService

    // I guess it is not needed or even small evil ))
    //@BeforeMapping
    //fun validateOrderBeforeConvertingToRest(source: AbstractFxCashOrder, @MappingTarget target: RestFxOrder) =
    //    source.validateCurrentState()

    @Mapping(source = "marketSymbol", target = "market")
    // to avoid warnings
    @Mapping(target = "limitPrice", ignore = true)
    @Mapping(target = "stopPrice", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun internalBaseFxCashAttrsToDto(source: AbstractFxCashOrder?, @MappingTarget target: DtoFxOrder?): DtoFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: FxCashLimitOrder?, @MappingTarget target: DtoFxOrder?): DtoFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "stopPrice")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun stopOrderToDto(source: FxCashStopOrder?, @MappingTarget target: DtoFxOrder?): DtoFxOrder?

    @InheritConfiguration(name = "internalBaseFxCashAttrsToDto")
    abstract fun marketOrderToDto(source: FxCashMarketOrder?, @MappingTarget target: DtoFxOrder?): DtoFxOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainAbstractFxCashOrder?): RestFxOrder? =
    //    if (source == null) null else
    //        when (source.orderType) {
    //            DomainOrderType.MARKET_ORDER -> marketOrderToDto(source as DomainFxCashMarketOrder)
    //            DomainOrderType.LIMIT_ORDER  -> limitOrderToDto(source as DomainFxCashLimitOrder)
    //            DomainOrderType.STOP_ORDER   -> stopOrderToDto(source as DomainFxCashStopOrder)
    //        }

    // T O D O: can we do it better without this switch?
    fun toDto(source: AbstractFxCashOrder?): DtoFxOrder? {
        val target = DtoFxOrder()
        return when (source) {
            is FxCashMarketOrder -> marketOrderToDto(source, target)
            is FxCashLimitOrder  -> limitOrderToDto(source, target)
            is FxCashStopOrder   -> stopOrderToDto(source, target)
            else -> null
        }
    }


    @Mapping(source = "market", target = "marketSymbol")
    @Mapping(target = "market",  ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "resultingPrice", ignore = true)
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun internalDtoToBaseFxCashAttrs(source: DtoFxOrder, @MappingTarget target: AbstractFxCashOrder): AbstractFxCashOrder

    @InheritConfiguration(name = "internalDtoToBaseFxCashAttrs")
    @Mapping(target = "limitPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getLimitPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToLimitOrder(source: DtoFxOrder, @MappingTarget target: FxCashLimitOrder): FxCashLimitOrder

    @InheritConfiguration(name = "internalDtoToBaseFxCashAttrs")
    @Mapping(target = "stopPrice", expression = "java( com.mvv.bank.orders.domain.Amount.of(source.getStopPrice(), target.getPriceCurrency()) )")
    abstract fun dtoToStopOrder(source: DtoFxOrder, @MappingTarget target: FxCashStopOrder): FxCashStopOrder

    @InheritConfiguration(name = "internalDtoToBaseFxCashAttrs")
    abstract fun dtoToMarketOrder(source: DtoFxOrder, @MappingTarget target: FxCashMarketOrder): FxCashMarketOrder

    @AfterMapping
    fun postInitDomainOrder(source: DtoFxOrder, @MappingTarget target: AbstractFxCashOrder) {
        // if (source == null) return
        target.market = marketService.marketBySymbol(source.market)

        target.validateCurrentState()

        if (source.orderType == DtoOrderType.MARKET_ORDER) {
            require(source.limitPrice == null && source.stopPrice == null) {
                "Market price cannot have limit/stop price (${source.limitPrice.safe}/${source.stopPrice.safe})." }
            require(source.dailyExecutionType == null) {
                "Market price cannot have daily execution type (${source.dailyExecutionType.safe})." }
        }
    }

    // T O D O: can we do it better without this switch?
    fun toDomain(source: DtoFxOrder): AbstractFxCashOrder {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val target = resolve<AbstractFxCashOrder>(source)
        return when (target) {
            is FxCashMarketOrder -> dtoToMarketOrder(source, target)
            is FxCashLimitOrder  -> dtoToLimitOrder(source, target)
            is FxCashStopOrder   -> dtoToStopOrder(source, target)
            //else -> null
        }
    }

    @ObjectFactory
    fun <T : AbstractFxCashOrder> resolve(source: FxOrder): T {
        val constructor = source.orderType.domainType.primaryConstructor
            ?.apply { if (!isAccessible) isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        return constructor!!.call() as T
    }

    // for easy testing
    public override fun clone(): FxOrderMapper = super.clone() as FxOrderMapper
}
