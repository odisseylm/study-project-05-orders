package com.mvv.bank.orders.domain

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


internal class MarketSymbolTest {

    @Test
    fun validate() {
        SoftAssertions().apply {

            assertThat(MarketSymbol.of("GOOGLE").value).isEqualTo("GOOGLE")
            assertThat(MarketSymbol.valueOf("GOOGLE").toString()).isEqualTo("GOOGLE")

            assertThat(MarketSymbol("GOOGLE.KT").value).isEqualTo("GOOGLE.KT")
            assertThat(MarketSymbol("GOOGLE-KT").value).isEqualTo("GOOGLE-KT")

            // Max length 25
            assertThat(MarketSymbol("A".repeat(25)).toString()).isEqualTo("A".repeat(25))
            assertThatCode { MarketSymbol("A".repeat(26)).toString() }
                .hasMessage("Invalid market symbol [AAAAAAAAAAAAAAAAAAAAAAAAAA].")

            assertThatCode { MarketSymbol("").toString() }
                .hasMessage("Invalid market symbol [].")
            assertThatCode { MarketSymbol(" ").toString() }
                .hasMessage("Invalid market symbol [ ].")
            assertThatCode { MarketSymbol(" GOOGLE").toString() }
                .hasMessage("Invalid market symbol [ GOOGLE].")
            assertThatCode { MarketSymbol("GOOGLE ").toString() }
                .hasMessage("Invalid market symbol [GOOGLE ].")
            assertThatCode { MarketSymbol("GOO GLE").toString() }
                .hasMessage("Invalid market symbol [GOO GLE].")

            assertThatCode { MarketSymbol("GOOGLE'KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE'KT].")
            assertThatCode { MarketSymbol("GOOGLE`KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE`KT].")
            assertThatCode { MarketSymbol("GOOGLE/KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE/KT].")
            assertThatCode { MarketSymbol("GOOGLE\\KT").toString() }
                .hasMessage("Invalid market symbol [GOOGLE\\KT].")

        }.assertAll()
    }
}
