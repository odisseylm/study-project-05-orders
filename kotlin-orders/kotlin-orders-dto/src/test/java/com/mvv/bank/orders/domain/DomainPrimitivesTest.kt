package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


class DomainPrimitivesTest {

    @Test
    fun validateEmail() {
        SoftAssertions().apply {

            assertThat(Email.of("vovan@gmail.com").toString()).isEqualTo("vovan@gmail.com")
            assertThat(Email.of("vovan.cheburan@gmail.com").toString()).isEqualTo("vovan.cheburan@gmail.com")

            assertThatCode { Email.of("") }.hasMessage("Email cannot be null/blank.")
            assertThatCode { Email.of(" ") }.hasMessage("Email cannot be null/blank.")

            assertThatCode { Email.of(" vovan@gmail.com") }.hasMessage("Invalid email [ vovan@gmail.com].")
            assertThatCode { Email.of("vovan@gmail.com ") }.hasMessage("Invalid email [vovan@gmail.com ].")
            assertThatCode { Email.of("vovan@ gmail.com") }.hasMessage("Invalid email [vovan@ gmail.com].")
            assertThatCode { Email.of("v'ovan@gmail.com") }.hasMessage("Invalid email [v'ovan@gmail.com].")
            assertThatCode { Email.of("v`ovan@gmail.com") }.hasMessage("Invalid email [v`ovan@gmail.com].")
            assertThatCode { Email.of("vovan/cheburan@gmail.com") }.hasMessage("Invalid email [vovan/cheburan@gmail.com].")

        }.assertAll()
    }

    @Test
    fun validatePhone() {
        SoftAssertions().apply {

            assertThat(Phone.of("+380661234567").toString()).isEqualTo("+380661234567")

            assertThatCode { Phone.of("") }.hasMessage("Phone number cannot be null/blank.")
            assertThatCode { Phone.of(" ") }.hasMessage("Phone number cannot be null/blank.")

            // short format is not allowed
            assertThatCode { Phone.of("0661234567") }.hasMessage("Invalid phone number [0661234567].")
            // separators are not allowed
            assertThatCode { Phone.of("+38(066)1234567") }.hasMessage("Invalid phone number [+38(066)1234567].")
            assertThatCode { Phone.of("+38 066 123 4567") }.hasMessage("Invalid phone number [+38 066 123 4567].")
            assertThatCode { Phone.of("+38'066'1234567") }.hasMessage("Invalid phone number [+38'066'1234567].")

            assertThatCode { Phone.of(" +380661234567") }.hasMessage("Invalid phone number [ +380661234567].")
            assertThatCode { Phone.of("+380661234567 ") }.hasMessage("Invalid phone number [+380661234567 ].")
            assertThatCode { Phone.of("+38 066 1234567") }.hasMessage("Invalid phone number [+38 066 1234567].")

        }.assertAll()
    }
}
