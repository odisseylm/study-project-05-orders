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
import com.mvv.nullables.{isNotNull, isNull}
import com.mvv.utils.{require, requireNotBlank, requireNotNull, equalImpl}
import com.mvv.collections.in
import com.mvv.log.safe


@Untainted @Immutable
case class Email private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String) derives CanEqual :
  validateEmail(value)
  @Untainted override def toString: String = this.value

/*
@Untainted @Immutable
class Email private (@(Tainted @param) @(Untainted @field @getter) val value: String)
  extends Equals derives CanEqual :

  validateEmail(value)

  @Untainted
  override def toString: String = this.value
  override def hashCode: Int = this.value.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[Email]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: Email => that.canEqual(this) && this.value == that.value
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { _.value == _.value }
*/

given givenCanEqual_Email_Null: CanEqual[Email, Null] = CanEqual.derived
given givenCanEqual_EmailNull_Null: CanEqual[Email|Null, Null] = CanEqual.derived
given givenCanEqual_EmailNull_Email: CanEqual[Email|Null, Email] = CanEqual.derived
given givenCanEqual_Null_Email: CanEqual[Null, Email] = CanEqual.derived
given givenCanEqual_Null_EmailNull: CanEqual[Null, Email|Null] = CanEqual.derived
given givenCanEqual_Email_EmailNull: CanEqual[Email, Email|Null] = CanEqual.derived


object Email :
  def apply(@Tainted email: String): Email = new Emai(email)

  // standard java methods to get from string. It can help to integrate with other java frameworks.
  def of(@Tainted email: String): Email = Email(email)
  def valueOf(@Tainted email: String): Email = Email(email)



// see https://www.baeldung.com/java-email-validation-regex
// RFC5322
// regex "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
// regex "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
//        + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
private val emailOwaspPattern = Regex("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
private def validateEmail(@Tainted email: String|Null): Unit =
  requireNotBlank(email, "Email cannot be null/blank.")
  if (!emailOwaspPattern.matches(email.nn)) throw IllegalArgumentException(s"Invalid email [${email.safe}].")


// see https://www.baeldung.com/java-regex-validate-phone-numbers
// regex "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
// regex "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$"
private val phonePattern = Regex("^\\+?[1-9][0-9]{7,14}$")
private def validatePhone(@Tainted email: String|Null): Unit =
  requireNotBlank(email, "Phone number cannot be null/blank.")
  if !phonePattern.matches(email.nn) then throw IllegalArgumentException(s"Invalid phone number [${email.safe}].")
