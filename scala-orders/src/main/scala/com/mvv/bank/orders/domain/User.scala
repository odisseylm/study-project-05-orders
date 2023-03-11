//noinspection ScalaUnusedSymbol // T O D O: remove after adding test and so on
package com.mvv.bank.orders.domain

import scala.language.strictEquality
//
import javax.annotation.{Tainted, Untainted}
import javax.annotation.concurrent.Immutable
import scala.annotation.meta.{field, getter, param}
import scala.annotation.unused
//
import com.mvv.nullables.NullableCanEqualGivens
import com.mvv.utils.equalImpl


// it is not clear now what it should be??? phone and email can be changed? or cannot?
@Untainted @Immutable
case class UserNaturalKey private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String) derives CanEqual :

  // now we will use email but later will change it
  Email(value) // used for validation only

  @Untainted override def toString: String = value

/*
@Untainted @Immutable
class UserNaturalKey private (
  @(Tainted @param) @(Untainted @field @getter)
  val value: String) extends Equals derives CanEqual :

  // now we will use email but later will change it
  Email(value) // used for validation only

  @Untainted
  override def toString: String = value
  override def hashCode: Int = value.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[UserNaturalKey]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: UserNaturalKey => that.canEqual(this) && this.value == that.value
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { _.value == _.value }
*/


object UserNaturalKey extends NullableCanEqualGivens[UserNaturalKey] :
  def apply(userNaturalKey: String): UserNaturalKey = new UserNaturalKey(userNaturalKey)
  // for java (MapStruct so on)
  def of(userNaturalKey: String): UserNaturalKey = apply(userNaturalKey)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  def valueOf(userNaturalKey: String): UserNaturalKey = apply(userNaturalKey)


// T O D O: think about natural-key
@Untainted @Immutable
case class User private (
  @(Tainted @param) @(Untainted @field @getter)
  naturalKey: UserNaturalKey) derives CanEqual :
  @Untainted def value: String = naturalKey.value
  @Untainted override def toString: String = s"User[$naturalKey]"

/*
// T O D O: think about natural-key
@Untainted @Immutable
class User private (
  @(Tainted @param) @(Untainted @field @getter)
  val naturalKey: UserNaturalKey) extends Equals derives CanEqual :
  @Untainted
  def value: String = naturalKey.value
  @Untainted
  override def toString: String = s"User[$naturalKey]"
  override def hashCode(): Int = value.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[User]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: User => that.canEqual(this) && this.value == that.value
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { _.value == _.value }
*/


object User extends NullableCanEqualGivens[User] :
  def apply(naturalKey: UserNaturalKey): User = new User(naturalKey)
  def apply(@Tainted naturalKey: String): User = new User(UserNaturalKey(naturalKey))
  // for java (MapStruct so on)
  def of(@Tainted naturalKey: String): User = apply(naturalKey)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  def valueOf(@Tainted naturalKey: String): User = apply(naturalKey)


/*
// T O D O: probably should be object which can be read only once
class Password (val value: String) {
    init {
        // T O D O: add validation
    }
}
*/


/*
// this class describes details which are not need for most cases/services
class UserDetails (
    val id: Long,
    val username: String,
    val email: Email,
    val phone: Phone,
    val password: String,
)
*/
