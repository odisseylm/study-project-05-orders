package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.log.safe
import com.mvv.bank.orders.conversion.CurrencyMapper
import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.service.CompanyService
import com.mvv.bank.orders.service.MarketService
import jakarta.inject.Inject
import org.mapstruct.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

import com.mvv.bank.orders.domain.StockStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.StockLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.StockMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.StockOrder as DomainBaseOrder

import com.mvv.bank.orders.repository.jpa.entities.OrderType as DtoOrderType
import com.mvv.bank.orders.repository.jpa.entities.StockOrder as DtoOrder
import com.mvv.bank.orders.repository.jpa.entities.StockOrder as DtoStockOrder


@Mapper(
    componentModel = "spring, default, cdi, jakarta, jsr330",
    uses = [CurrencyMapper::class],
    imports = [Currency::class, Amount::class]
)
@Suppress("CdiInjectionPointsInspection")
abstract class StockOrderMapper : Cloneable {
    @Inject
    private lateinit var marketService: MarketService
    @Inject
    private lateinit var companyService: CompanyService

    fun mapUser(user: String): User = User.of(user) // TODO: move to base interface
    fun mapUser(user: User): String = user.value    // TODO: move to base interface

    @BeforeMapping
    open fun validateOrderBeforeSaving(source: DomainBaseOrder, @MappingTarget target: DtoStockOrder) =
        source.validateCurrentState()

    @Mapping(source = "marketSymbol", target = "market")
    @Mapping(source = "resultingPrice.value", target = "resultingPrice")
    @Mapping(source = "resultingQuote.bid.value", target = "resultingQuoteBid")
    @Mapping(source = "resultingQuote.ask.value", target = "resultingQuoteAsk")
    @Mapping(source = "resultingQuote.dateTime", target = "resultingQuoteTimestamp")
    // to avoid warnings
    @Mapping(target = "limitStopPrice", ignore = true)
    @Mapping(target = "priceCurrency", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun internalBaseStockAttrsToDto(source: DomainBaseOrder?, @MappingTarget target: DtoStockOrder?): DtoStockOrder?

    @InheritConfiguration(name = "internalBaseStockAttrsToDto")
    @Mapping(source = "limitPrice.value", target = "limitStopPrice")
    @Mapping(source = "limitPrice.currency", target = "priceCurrency")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoStockOrder?): DtoStockOrder?

    @InheritConfiguration(name = "internalBaseStockAttrsToDto")
    @Mapping(source = "stopPrice.value", target = "limitStopPrice")
    @Mapping(source = "stopPrice.currency", target = "priceCurrency")
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType")
    abstract fun stopOrderToDto(source: DomainStopOrder?, @MappingTarget target: DtoStockOrder?): DtoStockOrder?

    @InheritConfiguration(name = "internalBaseStockAttrsToDto")
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
    fun toDto(source: DomainBaseOrder?): DtoStockOrder? {
        val target = DtoStockOrder()
        return when (source) {
            is DomainMarketOrder -> marketOrderToDto(source, target)
            is DomainLimitOrder  -> limitOrderToDto(source, target)
            is DomainStopOrder   -> stopOrderToDto(source, target)
            else -> null
        }
    }


    @Mapping(source = "market", target = "marketSymbol")
    @Mapping(target = "market",  ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "resultingPrice", expression = "java( ( source.getResultingPrice() == null ? null : Amount.of(source.getResultingPrice(), Currency.of(source.getPriceCurrency())) ) )")
    //@Mapping(target = "resultingQuote", expression = "java( new StockQuote(source.getMarket(), source.getProduct(), source.getResultingQuoteTimestamp(),   ) )")
    @Mapping(target = "resultingQuote", ignore = true)
    abstract fun internalDtoToBaseStockAttrs(source: DtoStockOrder, @MappingTarget target: DomainBaseOrder): DomainBaseOrder

    @InheritConfiguration(name = "internalDtoToBaseStockAttrs")
    @Mapping(target = "limitPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract fun dtoToLimitOrder(source: DtoStockOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "internalDtoToBaseStockAttrs")
    @Mapping(target = "stopPrice", expression = "java( Amount.of(source.getLimitStopPrice(), Currency.of(source.getPriceCurrency())) )")
    abstract fun dtoToStopOrder(source: DtoStockOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "internalDtoToBaseStockAttrs")
    abstract fun dtoToMarketOrder(source: DtoStockOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    open fun postInitDomainOrder(source: DtoStockOrder, @MappingTarget target: DomainBaseOrder) {
        // if (source == null) return
        target.market = marketService.marketBySymbol(source.market)
        target.company = companyService.companyBySymbol(source.product)

        if (source.resultingQuoteTimestamp != null) {
            target.resultingQuote = StockQuote.of(
                target.market, target.company, source.resultingQuoteTimestamp!!,
                bid = source.resultingQuoteBid!!, ask = source.resultingQuoteAsk!!, Currency.of(source.priceCurrency),
            )
        }

        if (source.orderType == DtoOrderType.MARKET_ORDER) { // TODO: move to base interface
            require(source.limitStopPrice == null) {
                "Market price cannot have limit/stop price (${source.limitStopPrice.safe})." }
            require(source.dailyExecutionType == null) {
                "Market price cannot have daily execution type (${source.dailyExecutionType.safe})." }
        }

        target.validateCurrentState()
    }

    // T O D O: can we do it better without this switch?
    fun toDomain(source: DtoStockOrder): DomainBaseOrder {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val target = resolve<DomainBaseOrder>(source)
        return when (target) {
            is DomainMarketOrder -> dtoToMarketOrder(source, target)
            is DomainLimitOrder  -> dtoToLimitOrder(source, target)
            is DomainStopOrder   -> dtoToStopOrder(source, target)
            //else -> null
        }
    }


    @ObjectFactory
    fun <T : DomainBaseOrder> resolve(source: DtoOrder): T {
        val constructor = source.orderType.stockDomainType.primaryConstructor
            ?.apply { if (!isAccessible) isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        return constructor!!.call() as T
    }

    public override fun clone(): StockOrderMapper = super.clone() as StockOrderMapper
}
