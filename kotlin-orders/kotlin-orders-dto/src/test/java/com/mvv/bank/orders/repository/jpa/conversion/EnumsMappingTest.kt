package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.jpa.SqlShortcutEnum
import com.mvv.bank.log.safe
import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.domain.OrderType
import com.mvv.bank.orders.repository.jpa.entities.OrderState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import com.mvv.bank.orders.repository.jpa.entities.OrderType as JpaOrderType
import com.mvv.bank.orders.domain.OrderType as DomainOrderType


class EnumTypesTest {

    @Suppress("RemoveRedundantQualifierName")
    @Test
    fun convert() {
        convertBetweenDomainAndJpa(
            OrderType::class,
            com.mvv.bank.orders.repository.jpa.entities.OrderType::class,
        )
        convertBetweenDomainAndJpa(
            Side::class,
            com.mvv.bank.orders.repository.jpa.entities.Side::class,
        )
        convertBetweenDomainAndJpa(
            BuySellType::class,
            com.mvv.bank.orders.repository.jpa.entities.BuySellType::class,
        )
        convertBetweenDomainAndJpa(
            DailyExecutionType::class,
            com.mvv.bank.orders.repository.jpa.entities.DailyExecutionType::class,
        )
        convertBetweenDomainAndJpa(
            com.mvv.bank.orders.domain.OrderState::class,
            OrderState::class,
        )
    }

    @Test
    fun orderTypeMapping() {
        JpaOrderType.values().forEach { assertThat(DomainOrderType.valueOf(it.name)).isNotNull() }
        DomainOrderType.values().forEach { assertThat(JpaOrderType.valueOf(it.name)).isNotNull() }

        val sqlLabels = JpaOrderType.values()
            .map { it.sqlShortcut }
            .distinct()
        assertThat(sqlLabels).hasSize(JpaOrderType.values().size)
    }
}


private fun <DomainEnumType: Enum<*>, JpaEnumType: Enum<*>> convertBetweenDomainAndJpa(
    domainType: KClass<DomainEnumType>,
    jpaType: KClass<JpaEnumType>,
    ) {
    assertThat(domainType).isNotEqualTo(jpaType)
    enumValues(domainType).forEach { assertThat(enumValueOf(jpaType, it.name)).isNotNull() }
    enumValues(jpaType).forEach { assertThat(enumValueOf(domainType, it.name)).isNotNull() }

    val jpaEnumValues = enumValues(jpaType)
    val sqlLabels = jpaEnumValues
        .map { (it as SqlShortcutEnum).sqlShortcut }
        .distinct()
    assertThat(sqlLabels).hasSize(jpaEnumValues.size)
}

@Suppress("UNCHECKED_CAST")
private fun <T : Enum<*>> enumValues(type: KClass<T>): Array<T> {
    val enums = type.declaredFunctions
        .find { it.name == "values" }
        ?.call() as Array<T>

    assertThat(enums).isNotNull.isNotEmpty
    return enums
}

@Suppress("UNCHECKED_CAST")
private fun <T : Enum<*>> enumValueOf(type: KClass<T>, enumName: String): T =
    enumValues(type)
        .find { it.name == enumName }
        ?: throw IllegalArgumentException("No enum [${enumName.safe}] of type ${type.qualifiedName}.")
