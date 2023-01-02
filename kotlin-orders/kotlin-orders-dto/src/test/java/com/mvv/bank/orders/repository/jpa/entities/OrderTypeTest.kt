package com.mvv.bank.orders.repository.jpa.entities

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test


class OrderTypeTest {

    @Test
    fun convertToDatabase() {

        val conv = OrderType.SqlConverter()

        assertThat(conv.convertToDatabaseColumn(OrderType.STOP_ORDER)).isEqualTo("STOP")

        assertThatCode { conv.convertToDatabaseColumn(null) }
            .hasMessage("Null value of OrderType is not allowed.")
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun convertFromDatabase() {

        val conv = OrderType.SqlConverter()

        assertThat(conv.convertToEntityAttribute("STOP")).isEqualTo(OrderType.STOP_ORDER)

        assertThatCode {         assertThat(conv.convertToEntityAttribute("S T O P")) }
            .hasMessage("No OrderType is found for SQL [S T O P].")
            .isExactlyInstanceOf(IllegalArgumentException::class.java)

        assertThatCode { conv.convertToDatabaseColumn(null) }
            .hasMessage("Null value of OrderType is not allowed.")
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
    }
}
