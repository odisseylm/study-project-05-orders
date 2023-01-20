package com.mvv.bank.orders.domain

import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable
import scala.annotation.meta.{field, getter, param}
import scala.annotation.unused

import com.mvv.utils.equalImpl


// it is not clear now what it should be??? phone and email can be changed? or cannot?
@Untainted @Immutable
case class UserNaturalKey private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String) derives CanEqual :

  // now we will use email but later will change it
  Email(value) // used for validation only

  @Untainted
  override def toString: String = value

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

given givenCanEqual_UserNaturalKey_Null: CanEqual[UserNaturalKey, Null] = CanEqual.derived
given givenCanEqual_UserNaturalKeyNull_Null: CanEqual[UserNaturalKey|Null, Null] = CanEqual.derived
given givenCanEqual_UserNaturalKeyNull_UserNaturalKey: CanEqual[UserNaturalKey|Null, UserNaturalKey] = CanEqual.derived
given givenCanEqual_Null_UserNaturalKey: CanEqual[Null, UserNaturalKey] = CanEqual.derived
given givenCanEqual_Null_UserNaturalKeyNull: CanEqual[Null, UserNaturalKey|Null] = CanEqual.derived
given givenCanEqual_UserNaturalKey_UserNaturalKeyNull: CanEqual[UserNaturalKey, UserNaturalKey|Null] = CanEqual.derived


object UserNaturalKey :
  def apply(userNaturalKey: String): UserNaturalKey = new UserNaturalKey(userNaturalKey)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  @unused
  def valueOf(userNaturalKey: String): UserNaturalKey = apply(userNaturalKey)


// T O D O: think about natural-key
@Untainted @Immutable
case class User private (
  @(Tainted @param) @(Untainted @field @getter)
  naturalKey: UserNaturalKey) derives CanEqual :
  @Untainted
  def value: String = naturalKey.value
  @Untainted
  override def toString: String = s"User[$naturalKey]"

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


given givenCanEqual_User_Null: CanEqual[User, Null] = CanEqual.derived
given givenCanEqual_UserNull_Null: CanEqual[User|Null, Null] = CanEqual.derived
given givenCanEqual_UserNull_User: CanEqual[User|Null, User] = CanEqual.derived
given givenCanEqual_Null_User: CanEqual[Null, User] = CanEqual.derived
given givenCanEqual_Null_UserNull: CanEqual[Null, User|Null] = CanEqual.derived
given givenCanEqual_User_UserNull: CanEqual[User, User|Null] = CanEqual.derived


object User :
  def apply(naturalKey: UserNaturalKey): User = new User(naturalKey)
  def apply(naturalKey: String): User = new User(UserNaturalKey(naturalKey))
  // standard java method to get from string. It can help to integrate with other java frameworks.
  @unused
  def valueOf(naturalKey: String): User = apply(naturalKey)


/*
// TODO: probably should be object which can be read only once
class Password (val value: String) {
    init {
        // TODO: add validation
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
