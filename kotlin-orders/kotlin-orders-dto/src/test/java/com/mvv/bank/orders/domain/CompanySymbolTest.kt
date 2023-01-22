package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


internal class CompanySymbolTest {

    @Test
    fun validate() {
        SoftAssertions().apply {

            assertThat(CompanySymbol.of("GOOGLE").value).isEqualTo("GOOGLE")
            assertThat(CompanySymbol.valueOf("GOOGLE").toString()).isEqualTo("GOOGLE")

            assertThat(CompanySymbol("GOOGLE.KT").value).isEqualTo("GOOGLE.KT")
            assertThat(CompanySymbol("GOOGLE-KT").value).isEqualTo("GOOGLE-KT")

            // Max length 25
            assertThat(CompanySymbol("A".repeat(25)).toString()).isEqualTo("A".repeat(25))
            assertThatCode { CompanySymbol("A".repeat(26)).toString() }
                .hasMessage("Invalid market symbol [AAAAAAAAAAAAAAAAAAAAAAAAAA].")

            assertThatCode { CompanySymbol("").toString() }
                .hasMessage("Invalid market symbol [].")
            assertThatCode { CompanySymbol(" ").toString() }
                .hasMessage("Invalid market symbol [ ].")
            assertThatCode { CompanySymbol(" GOOGLE").toString() }
                .hasMessage("Invalid market symbol [ GOOGLE].")
            assertThatCode { CompanySymbol("GOOGLE ").toString() }
                .hasMessage("Invalid market symbol [GOOGLE ].")
            assertThatCode { CompanySymbol("GOO GLE").toString() }
                .hasMessage("Invalid market symbol [GOO GLE].")

            assertThatCode { CompanySymbol("GOOGLE'KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE'KT].")
            assertThatCode { CompanySymbol("GOOGLE`KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE`KT].")
            assertThatCode { CompanySymbol("GOOGLE/KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE/KT].")
            assertThatCode { CompanySymbol("GOOGLE\\KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE\\KT].")

        }.assertAll()
    }

}