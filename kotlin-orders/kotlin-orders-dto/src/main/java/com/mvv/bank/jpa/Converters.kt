package com.mvv.bank.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter


// just proposed interface ))
interface SqlShortcutEnum {
    val sqlShortcut: String
}


@Converter
open class AttributeConverterAdapter<T, SqlType>(
    private val valueToSqlText: (T)->SqlType?,
    private val sqlTextToEnum: (SqlType)->T?,
) : AttributeConverter<T?, SqlType?> {
    override fun convertToDatabaseColumn(value: T?): SqlType? = if (value == null) null else valueToSqlText(value)

    override fun convertToEntityAttribute(sqlShortcut: SqlType?): T? =
        if (sqlShortcut == null) null else sqlTextToEnum(sqlShortcut)
}


@Converter
open class SqlShortcutEnumConverter<T>(
    sqlTextToEnum: (String)->T?
    ) : AttributeConverterAdapter<T, String>(
        valueToSqlText = { it.sqlShortcut },
        sqlTextToEnum = sqlTextToEnum,
    ) where T: Enum<*>, T: SqlShortcutEnum
