package com.mvv.bank.orders.repository.jpa.entities

import com.mvv.bank.jpa.SqlShortcutEnum
import com.mvv.bank.jpa.SqlShortcutEnumConverter
import com.mvv.bank.jpa.SqlShortcutEnumFactory
import com.mvv.bank.orders.domain.*
import jakarta.persistence.Converter
import kotlin.reflect.KClass


enum class OrderType (
    override val sqlShortcut: String,
    val cashDomainType: KClass<out AbstractFxCashOrder>,
    val stockDomainType: KClass<out com.mvv.bank.orders.domain.StockOrder>,
    ) : SqlShortcutEnum {
    MARKET_ORDER("MKT", FxCashMarketOrder::class, StockMarketOrder::class),
    LIMIT_ORDER("LIM", FxCashLimitOrder::class, StockLimitOrder::class),
    STOP_ORDER("STOP", FxCashStopOrder::class, StockStopOrder::class),
    ;

    companion object {
        private val factory = SqlShortcutEnumFactory(OrderType::class)
    }

    @Converter(autoApply = true)
    class SqlConverter : SqlShortcutEnumConverter<OrderType>(sqlTextToEnum = { factory.fromSql(it) })
}


enum class Side (override val sqlShortcut: String) : SqlShortcutEnum {
    CLIENT("C"),
    BANK_MARKET("BM"),
    ;

    companion object {
        private val factory = SqlShortcutEnumFactory(Side::class)
    }

    @Converter(autoApply = true)
    class SqlConverter : SqlShortcutEnumConverter<Side>(sqlTextToEnum = { factory.fromSql(it) })
}

//val <T : Any> KClass<T>.companionClass get() =
//    if (isCompanion) this.java.declaringClass else null

enum class BuySellType (override val sqlShortcut: String) : SqlShortcutEnum {
    BUY("B"),
    SELL("S"),
    ;

    companion object {
        private val factory = SqlShortcutEnumFactory(BuySellType::class)
    }

    @Converter(autoApply = true)
    class SqlConverter : SqlShortcutEnumConverter<BuySellType>(sqlTextToEnum = { factory.fromSql(it) })
}


enum class DailyExecutionType (override val sqlShortcut: String) : SqlShortcutEnum {
    DAY_ONLY("D"),
    GTC("GTC"),
    ;

    companion object {
        private val factory = SqlShortcutEnumFactory(DailyExecutionType::class)
    }

    @Converter(autoApply = true)
    class SqlConverter : SqlShortcutEnumConverter<DailyExecutionType>(sqlTextToEnum = { factory.fromSql(it) })
}


enum class OrderState (override val sqlShortcut: String) : SqlShortcutEnum {
    UNKNOWN(""),
    TO_BE_PLACED("-TP"), // it should not be put to database
    PLACED("P"),
    EXECUTED("X"),
    EXPIRED("EXP"),
    CANCELED("CAN"),
    ;

    override fun validateBeforeSave() {
        check(this != UNKNOWN) { "Order state is not set." }
        check(this != TO_BE_PLACED) { "Order state TO_BE_PLACED is designed only for passing to create order by separate SOA." }
        super.validateBeforeSave()
    }

    companion object {
        private val factory = SqlShortcutEnumFactory(OrderState::class)
    }

    @Converter(autoApply = true)
    class SqlConverter : SqlShortcutEnumConverter<OrderState>(sqlTextToEnum = { factory.fromSql(it) })
}
