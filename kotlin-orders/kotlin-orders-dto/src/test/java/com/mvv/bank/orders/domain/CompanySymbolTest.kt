package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


class CompanySymbolTest {

    @Test
    fun validate() {
        SoftAssertions().apply {

            assertThat(CompanySymbol.of("GOOGLE").value).isEqualTo("GOOGLE")
            assertThat(CompanySymbol.of("GOOGLE").toString()).isEqualTo("GOOGLE")

            assertThat(CompanySymbol.of("GOOGLE.KT").value).isEqualTo("GOOGLE.KT")
            assertThat(CompanySymbol.of("GOOGLE-KT").value).isEqualTo("GOOGLE-KT")

            // Max length 25
            assertThat(CompanySymbol.of("A".repeat(25)).toString()).isEqualTo("A".repeat(25))
            assertThatCode { CompanySymbol.of("A".repeat(26)).toString() }
                .hasMessage("Invalid market symbol [AAAAAAAAAAAAAAAAAAAAAAAAAA].")

            assertThatCode { CompanySymbol.of("").toString() }
                .hasMessage("Invalid market symbol [].")
            assertThatCode { CompanySymbol.of(" ").toString() }
                .hasMessage("Invalid market symbol [ ].")
            assertThatCode { CompanySymbol.of(" GOOGLE").toString() }
                .hasMessage("Invalid market symbol [ GOOGLE].")
            assertThatCode { CompanySymbol.of("GOOGLE ").toString() }
                .hasMessage("Invalid market symbol [GOOGLE ].")
            assertThatCode { CompanySymbol.of("GOO GLE").toString() }
                .hasMessage("Invalid market symbol [GOO GLE].")

            assertThatCode { CompanySymbol.of("GOOGLE'KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE'KT].")
            assertThatCode { CompanySymbol.of("GOOGLE`KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE`KT].")
            assertThatCode { CompanySymbol.of("GOOGLE/KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE/KT].")
            assertThatCode { CompanySymbol.of("GOOGLE\\KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE\\KT].")

        }.assertAll()
    }

}