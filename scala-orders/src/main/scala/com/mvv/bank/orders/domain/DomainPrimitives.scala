//noinspection ScalaUnusedSymbol // T O D O: remove after adding test and so on
package com.mvv.bank.orders.domain

import scala.language.strictEquality
//
import scala.annotation.meta.{getter, field, param}
import scala.annotation.unused
import scala.util.matching.Regex
//
import javax.annotation.{Tainted, Untainted}
import javax.annotation.concurrent.Immutable
//
import com.mvv.nullables.{isNotNull, isNull, NullableCanEqualGivens}
import com.mvv.utils.{require, requireNotBlank, requireNotNull, equalImpl}
import com.mvv.collections.in
import com.mvv.log.safe


@Untainted @Immutable
case class Email private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String) derives CanEqual :
  validateEmail(value)
  @Untainted override def toString: String = this.value


object Email extends NullableCanEqualGivens[Email] :
  def apply(@Tainted email: String): Email = new Email(email)

  // standard java methods to get from string. It can help to integrate with other java frameworks.
  def of(@Tainted email: String): Email = Email(email)
  def valueOf(@Tainted email: String): Email = Email(email)



@Untainted @Immutable
case class Phone private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String) derives CanEqual :
  validatePhone(value)
  @Untainted override def toString: String = this.value


object Phone extends NullableCanEqualGivens[Phone] :
  def apply(@Tainted phone: String): Phone = new Phone(phone)

  // standard java methods to get from string. It can help to integrate with other java frameworks.
  def of(@Tainted phone: String): Phone = Phone(phone)
  def valueOf(@Tainted phone: String): Phone = Phone(phone)



// see https://www.baeldung.com/java-email-validation-regex
// RFC5322
// regex "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
// regex "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
//        + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
private val emailOwaspPattern = Regex("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
private def validateEmail(@Tainted email: String): Unit =
  requireNotBlank(email, "Email cannot be null/blank.")
  if (!emailOwaspPattern.matches(email.nn)) throw IllegalArgumentException(s"Invalid email [${email.safe}].")


// see https://www.baeldung.com/java-regex-validate-phone-numbers
// regex "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
// regex "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$"
private val phonePattern = Regex("^\\+?[1-9][0-9]{7,14}$")
private def validatePhone(@Tainted phone: String): Unit =
  requireNotBlank(phone, "Phone number cannot be null/blank.")
  if !phonePattern.matches(phone.nn) then throw IllegalArgumentException(s"Invalid phone number [${phone.safe}].")
