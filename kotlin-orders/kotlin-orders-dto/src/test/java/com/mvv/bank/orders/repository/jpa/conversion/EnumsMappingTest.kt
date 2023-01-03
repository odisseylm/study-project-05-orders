package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.jpa.SqlShortcutEnum
import com.mvv.bank.log.safe
import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.domain.OrderType
import com.mvv.bank.orders.repository.jpa.entities.OrderState
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import com.mvv.bank.orders.repository.jpa.entities.OrderType as JpaOrderType
import com.mvv.bank.orders.domain.OrderType as DomainOrderType


class EnumTypesTest {

    @Suppress("RemoveRedundantQualifierName")
    @Test
    fun convert() {
        SoftAssertions().apply {

            convertBetweenDomainAndJpa(
                OrderType::class,
                com.mvv.bank.orders.repository.jpa.entities.OrderType::class,
                this,
            )
            convertBetweenDomainAndJpa(
                Side::class,
                com.mvv.bank.orders.repository.jpa.entities.Side::class,
                this,
            )
            convertBetweenDomainAndJpa(
                BuySellType::class,
                com.mvv.bank.orders.repository.jpa.entities.BuySellType::class,
                this,
            )
            convertBetweenDomainAndJpa(
                DailyExecutionType::class,
                com.mvv.bank.orders.repository.jpa.entities.DailyExecutionType::class,
                this,
            )
            convertBetweenDomainAndJpa(
                com.mvv.bank.orders.domain.OrderState::class,
                OrderState::class,
                this,
            )

        }.assertAll()
    }

    @Test
    fun orderTypeMapping() {
        SoftAssertions().apply {

            JpaOrderType.values().forEach { assertThat(DomainOrderType.valueOf(it.name)).isNotNull() }
            DomainOrderType.values().forEach { assertThat(JpaOrderType.valueOf(it.name)).isNotNull() }

            val sqlLabels = JpaOrderType.values()
                .map { it.sqlShortcut }
                .distinct()
            assertThat(sqlLabels).hasSize(JpaOrderType.values().size)

        }.assertAll()
    }
}


private fun <DomainEnumType: Enum<*>, JpaEnumType: Enum<*>> convertBetweenDomainAndJpa(
    domainType: KClass<DomainEnumType>,
    jpaType: KClass<JpaEnumType>,
    assertions: SoftAssertions,
    ) {
    assertions.assertThat(domainType).isNotEqualTo(jpaType)
    enumValues(domainType).forEach { assertions.assertThat(enumValueOf(jpaType, it.name)).isNotNull() }
    enumValues(jpaType).forEach { assertions.assertThat(enumValueOf(domainType, it.name)).isNotNull() }

    val jpaEnumValues = enumValues(jpaType)
    val sqlLabels = jpaEnumValues
        .map { (it as SqlShortcutEnum).sqlShortcut }
        .distinct()
    assertions.assertThat(sqlLabels).hasSize(jpaEnumValues.size)
}

@Suppress("UNCHECKED_CAST")
private fun <T : Enum<*>> enumValues(type: KClass<T>): Array<T> {
    val enums = type.declaredFunctions
        .find { it.name == "values" }
        ?.call() as Array<T>

    //checkNotNull(enums)
    check(enums.isNotEmpty())
    return enums
}

@Suppress("UNCHECKED_CAST")
private fun <T : Enum<*>> enumValueOf(type: KClass<T>, enumName: String): T =
    enumValues(type)
        .find { it.name == enumName }
        ?: throw IllegalArgumentException("No enum [${enumName.safe}] of type ${type.qualifiedName}.")
