package com.mvv.bank.orders.repository.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

enum class JpaOrderType (val sql: String) {
    MARKET_ORDER("MKT"),
    LIMIT_ORDER("LIM"),
    STOP_ORDER("STP"),
    BUY_STOP_ORDER("BST"),
    ;

    companion object {
        private val sqlToEnum = JpaOrderType.values().associateBy { en -> en.sql }

        fun fromSql(sql: String?): JpaOrderType? = sqlToEnum[sql]
    }
}

@Converter
class OrderTypeConverter : AttributeConverter<JpaOrderType?, String?> {
    override fun convertToDatabaseColumn(orderType: JpaOrderType?): String? = orderType?.sql

    override fun convertToEntityAttribute(orderType: String?): JpaOrderType? =
        if (orderType == null) null else JpaOrderType.fromSql(orderType)
}
