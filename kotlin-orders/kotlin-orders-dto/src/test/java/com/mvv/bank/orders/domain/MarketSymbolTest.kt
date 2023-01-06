package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


internal class MarketSymbolTest {

    @Test
    fun validate() {
        SoftAssertions().apply {

            assertThat(MarketSymbol.of("GOOGLE").value).isEqualTo("GOOGLE")
            assertThat(MarketSymbol.of("GOOGLE").toString()).isEqualTo("GOOGLE")

            assertThat(MarketSymbol.of("GOOGLE.KT").value).isEqualTo("GOOGLE.KT")
            assertThat(MarketSymbol.of("GOOGLE-KT").value).isEqualTo("GOOGLE-KT")

            // Max length 25
            assertThat(MarketSymbol.of("A".repeat(25)).toString()).isEqualTo("A".repeat(25))
            assertThatCode { MarketSymbol.of("A".repeat(26)).toString() }
                .hasMessage("Invalid market symbol [AAAAAAAAAAAAAAAAAAAAAAAAAA].")

            assertThatCode { MarketSymbol.of("").toString() }
                .hasMessage("Invalid market symbol [].")
            assertThatCode { MarketSymbol.of(" ").toString() }
                .hasMessage("Invalid market symbol [ ].")
            assertThatCode { MarketSymbol.of(" GOOGLE").toString() }
                .hasMessage("Invalid market symbol [ GOOGLE].")
            assertThatCode { MarketSymbol.of("GOOGLE ").toString() }
                .hasMessage("Invalid market symbol [GOOGLE ].")
            assertThatCode { MarketSymbol.of("GOO GLE").toString() }
                .hasMessage("Invalid market symbol [GOO GLE].")

            assertThatCode { MarketSymbol.of("GOOGLE'KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE'KT].")
            assertThatCode { MarketSymbol.of("GOOGLE`KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE`KT].")
            assertThatCode { MarketSymbol.of("GOOGLE/KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE/KT].")
            assertThatCode { MarketSymbol.of("GOOGLE\\KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE\\KT].")

        }.assertAll()
    }
}
