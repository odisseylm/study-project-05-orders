//noinspection ScalaUnusedSymbol
package com.mvv.bank.orders.domain


import scala.language.strictEquality
//
import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable
//
import scala.annotation.meta.{field, getter, param}
import scala.util.matching.Regex
//
import com.mvv.log.safe
import com.mvv.nullables.NullableCanEqualGivens



// most probably shares, derivatives, and others should be inherited from it
//interface Stock {
//    val name: String
//    val symbol: String
//}


@Untainted @Immutable
case class CompanySymbol private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String) derives CanEqual :
  validateCompanySymbol(value)
  @Untainted override def toString: String = value

object CompanySymbol extends NullableCanEqualGivens[CompanySymbol] :
  def apply(companySymbol: String) = new CompanySymbol(companySymbol)

  // standard java method to get from string. It can help to integrate with other java frameworks.
  def of(companySymbol: String): CompanySymbol = CompanySymbol(companySymbol)
  def valueOf(companySymbol: String): CompanySymbol = CompanySymbol(companySymbol)


trait Company :
  def name: String
  def symbol: CompanySymbol // see https://stockanalysis.com/stocks/  https://www.investopedia.com/terms/s/stocksymbol.asp
  def isin: String



trait CompanyProvider :
  // in general factory should return company data where Company.symbol = original symbol
  def companyBySymbol(companySymbol: CompanySymbol): Company

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


private val COMPANY_SYMBOL_MAX_LENGTH = 25
//private val companySymbolPattern = Regex("^[A-Z0-9\\-.]*$")
private val companySymbolPattern = Regex("^[A-Z0-9\\-.]*")

private def validateCompanySymbol(marketSymbol: String|Null): Unit =
  import com.mvv.nullables.AnyCanEqualGivens.given
  if (marketSymbol == null ||
      marketSymbol.isBlank ||
      marketSymbol.length > COMPANY_SYMBOL_MAX_LENGTH ||
     !companySymbolPattern.matches(marketSymbol))
    throw IllegalArgumentException(s"Invalid market symbol [${marketSymbol.safe}].")
