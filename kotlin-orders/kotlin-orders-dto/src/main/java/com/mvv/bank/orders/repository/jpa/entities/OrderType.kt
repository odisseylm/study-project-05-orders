package com.mvv.bank.orders.repository.jpa.entities

import com.mvv.bank.jpa.SqlShortcutEnum
import com.mvv.bank.jpa.SqlShortcutEnumConverter
import jakarta.persistence.Converter


enum class OrderType (override val sqlShortcut: String) : SqlShortcutEnum {
    MARKET_ORDER("MKT"),
    LIMIT_ORDER("LIM"),
    STOP_ORDER("STP"),
    ;

    companion object {
        private val sqlToEnum = OrderType.values().associateBy { en -> en.sqlShortcut }

        fun fromSql(sql: String?): OrderType? = sqlToEnum[sql]
    }

    @Converter
    class SqlConverter : SqlShortcutEnumConverter<OrderType>(sqlTextToEnum = { OrderType.fromSql(it) })
}
