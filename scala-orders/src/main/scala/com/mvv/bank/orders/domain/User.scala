package com.mvv.bank.orders.domain

import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable
import scala.annotation.meta.{field, getter, param}
import scala.annotation.unused


// it is not clear now what it should be??? phone and email can be changed? or cannot?
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
  override def equals(other: Any): Boolean = other match
    case that: UserNaturalKey => (that canEqual this) && this.value == that.value
    case _ => false


object UserNaturalKey :
  def apply(userNaturalKey: String): UserNaturalKey = new UserNaturalKey(userNaturalKey)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  @unused
  def valueOf(userNaturalKey: String): UserNaturalKey = apply(userNaturalKey)


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
  override def equals(other: Any): Boolean = other match
    case that: User => (that canEqual this) && this.value == that.value
    case _ => false

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
