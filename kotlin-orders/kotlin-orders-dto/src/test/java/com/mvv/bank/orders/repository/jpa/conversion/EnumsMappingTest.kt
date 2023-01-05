package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.jpa.SqlShortcutEnum
import com.mvv.bank.log.safe
import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.rest.entities.BuySellType
import com.mvv.bank.orders.rest.entities.DailyExecutionType
import com.mvv.bank.orders.rest.entities.OrderState
import com.mvv.bank.orders.rest.entities.OrderType
import com.mvv.bank.orders.rest.entities.Side
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions


internal class EnumTypesTest {

    @Suppress("RemoveRedundantQualifierName")
    @Test
    fun convert() {
        SoftAssertions().apply {
            val assertions = this

            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.OrderType::class,
                com.mvv.bank.orders.repository.jpa.entities.OrderType::class,
                assertions,
            )
            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.OrderType::class,
                OrderType::class,
                assertions,
            )

            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.Side::class,
                com.mvv.bank.orders.repository.jpa.entities.Side::class,
                assertions,
            )
            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.Side::class,
                Side::class,
                assertions,
            )

            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.BuySellType::class,
                com.mvv.bank.orders.repository.jpa.entities.BuySellType::class,
                assertions,
            )
            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.BuySellType::class,
                BuySellType::class,
                assertions,
            )

            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.DailyExecutionType::class,
                com.mvv.bank.orders.repository.jpa.entities.DailyExecutionType::class,
                assertions,
            )
            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.DailyExecutionType::class,
                DailyExecutionType::class,
                assertions,
            )

            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.OrderState::class,
                com.mvv.bank.orders.repository.jpa.entities.OrderState::class,
                assertions,
            )
            convertBetweenDomainAndDto(
                com.mvv.bank.orders.domain.OrderState::class,
                OrderState::class,
                assertions,
            )

        }.assertAll()
    }

    @Test
    fun orderTypeMapping() {
        SoftAssertions().apply {

            com.mvv.bank.orders.repository.jpa.entities.OrderType.values()
                .forEach { assertThat(com.mvv.bank.orders.domain.OrderType.valueOf(it.name)).isNotNull() }
            com.mvv.bank.orders.domain.OrderType.values()
                .forEach { assertThat(com.mvv.bank.orders.repository.jpa.entities.OrderType.valueOf(it.name)).isNotNull() }

            val sqlLabels = com.mvv.bank.orders.repository.jpa.entities.OrderType.values()
                .map { it.sqlShortcut }
                .distinct()
            assertThat(sqlLabels).hasSize(com.mvv.bank.orders.repository.jpa.entities.OrderType.values().size)

        }.assertAll()
    }
}


private fun <DomainEnumType: Enum<*>, JpaEnumType: Enum<*>> convertBetweenDomainAndDto(
    domainType: KClass<DomainEnumType>,
    jpaType: KClass<JpaEnumType>,
    assertions: SoftAssertions,
    ) {
    assertions.assertThat(domainType).isNotEqualTo(jpaType)
    enumValues(domainType).forEach { assertions.assertThat(enumValueOf(jpaType, it.name)).isNotNull() }
    enumValues(jpaType).forEach { assertions.assertThat(enumValueOf(domainType, it.name)).isNotNull() }

    val jpaEnumValues = enumValues(jpaType)
    if (jpaEnumValues[0] is SqlShortcutEnum) {
        val sqlLabels = jpaEnumValues
            .map { (it as SqlShortcutEnum).sqlShortcut }
            .distinct()
        assertions.assertThat(sqlLabels).hasSize(jpaEnumValues.size)
    }
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
