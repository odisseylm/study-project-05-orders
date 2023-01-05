package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.conversion.DomainPrimitiveMappers
import com.mvv.bank.orders.conversion.MAP_STRUCT_COMPONENT_MODEL
import org.mapstruct.*

import com.mvv.bank.orders.rest.entities.StockOrder as DtoOrder

import com.mvv.bank.orders.domain.StockOrder as DomainOrder
import com.mvv.bank.orders.domain.StockStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.StockLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.StockMarketOrder as DomainMarketOrder


@Mapper(
    componentModel = MAP_STRUCT_COMPONENT_MODEL,
    config = DomainPrimitiveMappers::class,
    uses = [
        StockQuoteMapper::class,
        AmountMapper::class,
    ]
)
@Suppress("CdiInjectionPointsInspection")
abstract class StockOrderMapper: AbstractRestOrderMapper() {

    // to avoid warnings
    @Mapping(target = "limitPrice", ignore = true)
    @Mapping(target = "stopPrice", ignore = true)
    //@Mapping(target = "priceCurrency", ignore = true)
    @Mapping(target = "dailyExecutionType", ignore = true)
    abstract fun baseOrderAttrsToDto(source: DomainOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "limitPrice", target = "limitPrice") // because earlier it was marked as ignored
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType") // because earlier it was marked as ignored
    abstract fun limitOrderToDto(source: DomainLimitOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    @Mapping(source = "stopPrice", target = "stopPrice") // because earlier it was marked as ignored
    @Mapping(source = "dailyExecutionType", target = "dailyExecutionType") // because earlier it was marked as ignored
    abstract fun stopOrderToDto(source: DomainStopOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    @InheritConfiguration(name = "baseOrderAttrsToDto")
    abstract fun marketOrderToDto(source: DomainMarketOrder?, @MappingTarget target: DtoOrder?): DtoOrder?

    // T O D O: can we do it better without this switch?
    //fun toDto(source: DomainAbstractFxCashOrder?): RestFxOrder? =
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

    /*
    @BeforeMapping
    @Suppress("UNUSED_PARAMETER") //, "unused")
    fun validateInputDtoOrder(source: DtoOrder, @MappingTarget target: Any?) {
        if (source.limitPrice != null || source.stopPrice != null) {
            checkNotNull(source.priceCurrency) {
                "Price currency is not set (however limit/stop price is [${source.limitPrice.safe}/${source.stopPrice.safe}/])" }
        }
    }
    */

    @Mapping(source = "product", target = "company")
    abstract fun baseOrderAttrsToDomain(source: DtoOrder, @MappingTarget target: DomainOrder): DomainOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToLimitOrder(source: DtoOrder, @MappingTarget target: DomainLimitOrder): DomainLimitOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToStopOrder(source: DtoOrder, @MappingTarget target: DomainStopOrder): DomainStopOrder

    @InheritConfiguration(name = "baseOrderAttrsToDomain")
    abstract fun dtoToMarketOrder(source: DtoOrder, @MappingTarget target: DomainMarketOrder): DomainMarketOrder

    @AfterMapping
    @Suppress("UNUSED_PARAMETER")
    fun postInitDomainOrder(source: DtoOrder, @MappingTarget target: DomainOrder) =
        target.validateCurrentState()

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
