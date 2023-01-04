package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


class DomainPrimitivesTest {

    @Test
    fun validateEmail() {
        SoftAssertions().apply {

            assertThat(Email("vovan@gmail.com").toString()).isEqualTo("vovan@gmail.com")
            assertThat(Email("vovan.cheburan@gmail.com").toString()).isEqualTo("vovan.cheburan@gmail.com")

            assertThatCode { Email("") }.hasMessage("Email cannot be null/blank.")
            assertThatCode { Email(" ") }.hasMessage("Email cannot be null/blank.")

            assertThatCode { Email(" vovan@gmail.com") }.hasMessage("Invalid email [ vovan@gmail.com].")
            assertThatCode { Email("vovan@gmail.com ") }.hasMessage("Invalid email [vovan@gmail.com ].")
            assertThatCode { Email("vovan@ gmail.com") }.hasMessage("Invalid email [vovan@ gmail.com].")
            assertThatCode { Email("v'ovan@gmail.com") }.hasMessage("Invalid email [v'ovan@gmail.com].")
            assertThatCode { Email("v`ovan@gmail.com") }.hasMessage("Invalid email [v`ovan@gmail.com].")
            assertThatCode { Email("vovan/cheburan@gmail.com") }.hasMessage("Invalid email [vovan/cheburan@gmail.com].")

        }.assertAll()
    }

    @Test
    fun validatePhone() {
        SoftAssertions().apply {

            assertThat(Phone("+380661234567").toString()).isEqualTo("+380661234567")

            assertThatCode { Phone("") }.hasMessage("Phone number cannot be null/blank.")
            assertThatCode { Phone(" ") }.hasMessage("Phone number cannot be null/blank.")

            // short format is not allowed
            assertThatCode { Phone("0661234567") }.hasMessage("Invalid phone number [0661234567].")
            // separators are not allowed
            assertThatCode { Phone("+38(066)1234567") }.hasMessage("Invalid phone number [+38(066)1234567].")
            assertThatCode { Phone("+38 066 123 4567") }.hasMessage("Invalid phone number [+38 066 123 4567].")
            assertThatCode { Phone("+38'066'1234567") }.hasMessage("Invalid phone number [+38'066'1234567].")

            assertThatCode { Phone(" +380661234567") }.hasMessage("Invalid phone number [ +380661234567].")
            assertThatCode { Phone("+380661234567 ") }.hasMessage("Invalid phone number [+380661234567 ].")
            assertThatCode { Phone("+38 066 1234567") }.hasMessage("Invalid phone number [+38 066 1234567].")

        }.assertAll()
    }
}
