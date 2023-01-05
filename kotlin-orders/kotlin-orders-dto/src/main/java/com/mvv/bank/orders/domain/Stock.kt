package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe
import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable


// most probably shares, derivatives, and others should be inherited from it
//interface Stock {
//    val name: String
//    val symbol: String
//}


@Untainted @Immutable
class CompanySymbol private constructor (@param:Tainted @field:Untainted val value: String) {
    init { validateCompanySymbol(value) }
    @Untainted
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as CompanySymbol).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        @JvmStatic fun of(companySymbol: String) = CompanySymbol(companySymbol)
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(companySymbol: String) = of(companySymbol)
    }
}


interface Company {
    val name: String
    val symbol: CompanySymbol // see https://stockanalysis.com/stocks/  https://www.investopedia.com/terms/s/stocksymbol.asp
    val isin: String
}


interface CompanyFactory {
    // in general factory should return company data where Company.symbol = original symbol
    fun getCompanyData(companySymbol: CompanySymbol): Company
}

/*
Ticker
Symbol  Name
AAPL    Apple Inc. Common Stock
MSFT    Microsoft Corporation Common St
TWTR    Twitter, Inc. (delisted)
GM      General Motors Company
JNJ     Johnson & Johnson Common Stock
ADSK    Autodesk
GOOG	Alphabet Inc. Class C Capital Stock
GOOGL	Alphabet Inc. Class A Common Stock
AMZN	Amazon.com, Inc. Common Stock
TSLA	Tesla, Inc. Common Stock
V       Visa Inc.
NVDA    NVIDIA Corporation Common Stock
WMT     Walmart Inc. Common Stock


BTC-USD	  Bitcoin USD
BTC-CAD   Bitcoin CAD
ETH-USD	  Ethereum USD
DOGE-USD  Dogecoin USD
EURUSD=X  EUR/USD
BNB-USD   BNB USD
ADA-USD   Cardano USD


Berkshire Hathaway Inc. (BRK-A)
NYSE - Nasdaq Real Time Price. Currency in USD

*/


private const val COMPANY_SYMBOL_MAX_LENGTH = 25
private val companySymbolPattern = Regex("^[A-Z0-9\\-.]*\$")

private fun validateCompanySymbol(marketSymbol: String?) {
    if (marketSymbol.isNullOrBlank() ||
        (marketSymbol.length > COMPANY_SYMBOL_MAX_LENGTH) ||
        !companySymbolPattern.matches(marketSymbol))
        throw IllegalArgumentException("Invalid market symbol [${marketSymbol.safe}].")
}
