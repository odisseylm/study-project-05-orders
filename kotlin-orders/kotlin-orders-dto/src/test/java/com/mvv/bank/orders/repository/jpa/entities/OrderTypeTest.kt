package com.mvv.bank.orders.repository.jpa.entities

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import com.mvv.bank.orders.repository.jpa.entities.OrderType as JpaOrderType
import com.mvv.bank.orders.domain.OrderType as DomainOrderType


class OrderTypeTest {

    @Test
    fun convertJpaToDomain() {
        JpaOrderType.values()
            .forEach { assertThat(DomainOrderType.valueOf(it.name)).isNotNull() }
    }

    @Test
    fun allJpaLabelsAreUnique() {
        val sqlLabels = JpaOrderType.values()
            .map { it.sqlShortcut }
            .distinct()

        assertThat(sqlLabels).hasSize(JpaOrderType.values().size)
    }

    @Test
    fun convertDomainToJpa() {
        DomainOrderType.values()
            .forEach { assertThat(JpaOrderType.valueOf(it.name)).isNotNull() }
    }
}
