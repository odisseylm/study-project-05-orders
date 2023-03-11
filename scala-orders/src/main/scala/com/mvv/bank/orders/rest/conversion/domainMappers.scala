package com.mvv.bank.orders.rest.conversion

import org.mapstruct.{ Mapper, ObjectFactory }
import com.mvv.bank.orders.domain.{
  CompanySymbol, Email, MarketSymbol, Phone, UserNaturalKey,
  Amount as DomainAmount, Currency as DomainCurrency, User as DomainUser,
}
import com.mvv.bank.orders.rest.entities.Amount as DtoAmount



//noinspection ScalaUnusedSymbol,ScalaFileName
trait DomainMappers :
  def currencyToDto(source: DomainCurrency): String = source.toString()
  def currencyToDomain(source: String): DomainCurrency = DomainCurrency(source)

  def userToDto(source: DomainUser): String = source.toString()
  def userToDomain(source: String): DomainUser = DomainUser(source)

  def marketSymbolToDto(marketSymbol: MarketSymbol): String = marketSymbol.value
  def marketSymbolToDomain(marketSymbol: String): MarketSymbol = MarketSymbol(marketSymbol)

  //noinspection ScalaWeakerAccess
  def amountToDto(source: DomainAmount): DtoAmount = DtoAmount(source.value, source.currency.value)
  def amountToDtoOption(source: DomainAmount): Option[DtoAmount] = Option(amountToDto(source))
  def amountOptToDtoOpt(source: Option[DomainAmount]): Option[DtoAmount] = source.map(s => amountToDto(s))

  //noinspection ScalaWeakerAccess
  def amountToDomain(source: DtoAmount): DomainAmount = DomainAmount.of(source.value, DomainCurrency.of(source.currency))
  def amountOptToDomain(source: Option[DtoAmount]): DomainAmount = source.map(s => amountToDomain(s))
    .getOrElse(throw IllegalThreadStateException("Required amount is not present in DTO."))
  def amountOptToDomainOpt(source: Option[DtoAmount]): Option[DomainAmount] = source.map(s => amountToDomain(s))


  //@ObjectFactory
  //def domainAmountToOption(v: DomainAmount): Option[DomainAmount] = Option(v)
  //def dtoAmountToOption(v: DtoAmount): Option[DtoAmount] = Option(v)
  //@ObjectFactory
  //def dtoAmountToOption(v: DtoAmount): Option[DtoAmount] = Option(v)
  //@ObjectFactory
  //def unwrap[T](v: Option[T]): T = v.get


  def userNaturalKeyToDto(userNaturalKey: UserNaturalKey): String = userNaturalKey.value
  def userNaturalKeyToDomain(userNaturalKey: String): UserNaturalKey = UserNaturalKey(userNaturalKey)

  def companySymbolToDto(companySymbol: CompanySymbol): String = companySymbol.value
  def companySymbolToDomain(companySymbol: String): CompanySymbol = CompanySymbol(companySymbol)

  def emailToDto(email: Email): String = email.value
  def emailToDomain(email: String): Email = Email(email)

  def phoneToDto(phone: Phone): String = phone.value
  def phoneToDomain(phone: String): Phone = Phone(phone)
