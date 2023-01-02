package com.mvv.bank.jpa

import com.mvv.bank.log.safe
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions


interface ValidDatabaseValue {
    val validDatabaseValue: Boolean get() = true // by default, we consider any non-null value as valid

    // it is not extension function to allow overriding
    fun validateBeforeSave() {
        if (!this.validDatabaseValue) throw IllegalArgumentException("Invalid database value ${this.safe}.")
    }

    fun validateAfterLoad() {
        if (!this.validDatabaseValue) throw IllegalArgumentException("Invalid database value ${this.safe}.")
    }
}


interface SqlShortcutEnum : ValidDatabaseValue {
    val sqlShortcut: String
    override val validDatabaseValue: Boolean get() = true
}

@Suppress("UNCHECKED_CAST")
private fun <T : Enum<*>> enumValues(type: KClass<T>): Array<T> =
    type.declaredFunctions
        .find { it.name == "values" }
        ?.call() as Array<T>


class SqlShortcutEnumFactory<T>(type: KClass<T>) where T: Enum<*>, T: SqlShortcutEnum {
    private val enumValues = enumValues(type)
    private val enumTypeSimpleName = enumValues[0].javaClass.simpleName
    private val sqlToEnum = enumValues
        .associateBy { en -> en.sqlShortcut }

    fun fromSql(sql: String?): T? {
        val value = sqlToEnum[sql]
        if (sql != null && value == null) throw IllegalArgumentException("No $enumTypeSimpleName is found for SQL [${sql}].")
        return value?.apply { validateAfterLoad() }
    }
}


@Converter
open class AttributeConverterAdapter<T, SqlType>(
    private val valueToSqlText: (T)->SqlType?,
    private val sqlTextToEnum: (SqlType)->T?,
    private val nullJavaValueAllowed: Boolean = false,
    private val nullSqlValueAllowed:  Boolean = false,
) : AttributeConverter<T?, SqlType?> {
    override fun convertToDatabaseColumn(value: T?): SqlType? {
        if (value == null && !nullJavaValueAllowed) throw IllegalArgumentException("Null value of $typeName is not allowed.")
        if (value is ValidDatabaseValue) value.validateBeforeSave()
        return if (value == null) null else valueToSqlText(value)
    }

    override fun convertToEntityAttribute(sqlShortcut: SqlType?): T? {
        if (sqlShortcut == null && !nullSqlValueAllowed) throw IllegalArgumentException("Null SQL value for $typeName is not allowed.")
        return if (sqlShortcut == null) null else sqlTextToEnum(sqlShortcut)
    }

    private val typeName: String get() {
        // T O D O: it works only if 1st parameter is type!
        val genericSuperclass: Type? = this.javaClass.genericSuperclass
        if (genericSuperclass is ParameterizedType && genericSuperclass.actualTypeArguments.isNotEmpty()) {
            return genericSuperclass.actualTypeArguments[0].typeName.substringAfterLast('.')
        }

        return "UnknownType"
    }
}


@Converter
open class SqlShortcutEnumConverter<T>(
    valueToSqlText: (T)->String? = { it.sqlShortcut },
    sqlTextToEnum: (String)->T?,
    nullJavaValueAllowed: Boolean = false,
    nullSqlValueAllowed:  Boolean = false,
    ) : AttributeConverterAdapter<T, String>(
        valueToSqlText = valueToSqlText,
        sqlTextToEnum = sqlTextToEnum,
        nullJavaValueAllowed = nullJavaValueAllowed,
        nullSqlValueAllowed = nullSqlValueAllowed,
    ) where T: Enum<*>, T: SqlShortcutEnum {
}
