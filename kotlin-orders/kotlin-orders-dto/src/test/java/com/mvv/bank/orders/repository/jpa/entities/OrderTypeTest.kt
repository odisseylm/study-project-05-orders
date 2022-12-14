package com.mvv.bank.orders.repository.jpa.entities

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


internal class OrderTypeTest {

    @Test
    fun convertToDatabase() {

        val conv = OrderType.SqlConverter()

        SoftAssertions().apply {

            assertThat(conv.convertToDatabaseColumn(OrderType.STOP_ORDER)).isEqualTo("STOP")

            assertThatCode { conv.convertToDatabaseColumn(null) }
                .hasMessage("Null value of OrderType is not allowed.")
                .isExactlyInstanceOf(IllegalArgumentException::class.java)

        }.assertAll()
    }

    @Test
    fun convertFromDatabase() {

        val conv = OrderType.SqlConverter()

        SoftAssertions().apply {

            assertThat(conv.convertToEntityAttribute("STOP")).isEqualTo(OrderType.STOP_ORDER)

            assertThatCode { conv.convertToEntityAttribute("S T O P") }
                .hasMessage("No OrderType is found for SQL [S T O P].")
                .isExactlyInstanceOf(IllegalArgumentException::class.java)

            assertThatCode { conv.convertToDatabaseColumn(null) }
                .hasMessage("Null value of OrderType is not allowed.")
                .isExactlyInstanceOf(IllegalArgumentException::class.java)

        }.assertAll()
    }
}
