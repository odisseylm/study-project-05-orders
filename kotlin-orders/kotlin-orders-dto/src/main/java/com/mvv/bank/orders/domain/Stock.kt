package com.mvv.bank.orders.domain


// most probably shares, derivatives, and others should be inherited from it
//interface Stock {
//    val name: String
//    val symbol: String
//}
interface Company {
    val name: String
    val symbol: String // see https://stockanalysis.com/stocks/  https://www.investopedia.com/terms/s/stocksymbol.asp
    @Suppress("PropertyName")
    val ISIN: String

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