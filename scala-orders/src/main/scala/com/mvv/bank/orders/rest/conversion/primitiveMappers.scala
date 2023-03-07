package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.domain.{
  CompanySymbol, Email, MarketSymbol, Phone, UserNaturalKey,
  Amount as DomainAmount, Currency as DomainCurrency, User as DomainUser,
}
import com.mvv.bank.orders.rest.entities.Amount as DtoAmount
import org.mapstruct.Mapper



//noinspection ScalaUnusedSymbol,ScalaFileName
trait DomainMappers :
  def currencyToDto(source: DomainCurrency): String = source.toString()
  def currencyToDomain(source: String): DomainCurrency = DomainCurrency(source)

  def userToDto(source: DomainUser): String = source.toString()
  def userToDomain(source: String): DomainUser = DomainUser(source)

  def marketSymbolToDto(marketSymbol: MarketSymbol): String = marketSymbol.value
  def marketSymbolToDomain(marketSymbol: String): MarketSymbol = MarketSymbol(marketSymbol)

  def amountToDto(source: DomainAmount): DtoAmount = DtoAmount(source.value, source.currency.value)
  def amountToDomain(source: DtoAmount): DomainAmount = DomainAmount.of(source.value, DomainCurrency.of(source.currency))

  def userNaturalKeyToDto(userNaturalKey: UserNaturalKey): String = userNaturalKey.value
  def userNaturalKeyToDomain(userNaturalKey: String): UserNaturalKey = UserNaturalKey(userNaturalKey)

  def companySymbolToDto(companySymbol: CompanySymbol): String = companySymbol.value
  def companySymbolToDomain(companySymbol: String): CompanySymbol = CompanySymbol(companySymbol)

  def emailToDto(email: Email): String = email.value
  def emailToDomain(email: String): Email = Email(email)

  def phoneToDto(phone: Phone): String = phone.value
  def phoneToDomain(phone: String): Phone = Phone(phone)
