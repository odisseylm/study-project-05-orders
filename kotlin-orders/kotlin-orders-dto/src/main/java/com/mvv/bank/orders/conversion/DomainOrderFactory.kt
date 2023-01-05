package com.mvv.bank.orders.conversion

/*
import com.mvv.bank.orders.domain.*
import com.mvv.bank.util.newInstance
import com.mvv.bank.util.newJavaInstance
import org.mapstruct.TargetType


/** It is only for MapStruct! It creates raw/uninitialized order instances. */
class DomainOrderFactory1 {
    fun createCashStopOrder(): FxCashStopOrder = newInstance()
    fun createCashLimitOrder(): FxCashLimitOrder = newInstance()
    fun createCashMarketOrder(): FxCashMarketOrder = newInstance()

    fun createStockStopOrder(): StockStopOrder = newInstance()
    fun createStockLimitOrder(): StockLimitOrder = newInstance()
    fun createStockMarketOrder(): StockMarketOrder = newInstance()
}

class DomainOrderFactory2 {
    fun <T: DomainBaseOrder> createEntity(@TargetType entityClass: Class<T>): T {
        return newJavaInstance(entityClass)
    }
}
*/
