package com.mvv.bank.orders.domain

import scala.language.strictEquality
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable

import com.mvv.utils.{isNotNull, isNull, require, requireNotBlank, requireNotNull}
import com.mvv.collections.in
import com.mvv.log.Logs.safe

import scala.util.matching.Regex
import scala.annotation.unused

@Untainted @Immutable
class Email private (/*@param:Tainted @field:Untainted*/ val value: String)
  extends Equals derives CanEqual :

  validateEmail(value)

  @Untainted
  override def toString: String = this.value
  override def hashCode: Int = this.value.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[Email]
  override def equals(other: Any): Boolean = other match
    case that: Email => (that canEqual this) && this.value == that.value
    case _ => false


object Email :
  def apply(email: String): Email = of(email)

  // Java style
  //@JvmStatic
  def of(email: String): Email = new Email(email)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  //@JvmStatic
  def valueOf(email: String): Email = of(email)



// see https://www.baeldung.com/java-email-validation-regex
// RFC5322
//private val emailRfc5322Pattern = Regex("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
// Strict Regular Expression Validation
//private val emailStrictPattern = Regex("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
//        + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
private val emailOwaspPattern = Regex("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
private def validateEmail(email: CharSequence|Null): Unit =
  requireNotBlank(email, "Email cannot be null/blank.")
  if (!emailOwaspPattern.matches(email.nn)) throw IllegalArgumentException(s"Invalid email [${email.safe}].")


// see https://www.baeldung.com/java-regex-validate-phone-numbers
//private val phonePattern = Regex("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$")
//private val phonePattern = Regex("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$")
private val phonePattern = Regex("^\\+?[1-9][0-9]{7,14}$")
private def validatePhone(email: CharSequence|Null): Unit =
//private def validatePhone(email: String|Null) =
  requireNotBlank(email, "Phone number cannot be null/blank.")
  if !phonePattern.matches(email.nn) then throw IllegalArgumentException(s"Invalid phone number [${email.safe}].")

