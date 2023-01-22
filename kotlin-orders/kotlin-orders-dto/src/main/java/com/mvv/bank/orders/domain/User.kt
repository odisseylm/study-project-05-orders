package com.mvv.bank.orders.domain

import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable


// it is not clear now what it should be??? phone and email can be changed? or cannot?
@Untainted @Immutable
class UserNaturalKey private constructor (@param:Tainted @field:Untainted val value: String) {
    init {
        // now we will use email but later will change it
        Email(value) // used for validation only
    }
    @Untainted
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as UserNaturalKey).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        operator fun invoke(userNaturalKey: String) = UserNaturalKey(userNaturalKey)

        // for java (MapStruct so on)
        @JvmStatic fun of(userNaturalKey: String) = invoke(userNaturalKey)
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(userNaturalKey: String) = invoke(userNaturalKey)
    }
}


// T O D O: think about natural-key
@Untainted @Immutable
class User private constructor (@param:Tainted @field:Untainted val naturalKey: UserNaturalKey) {
    @get:Untainted
    val value: String get() = naturalKey.value
    @Untainted
    override fun toString(): String = "User[${naturalKey}]"
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as User).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        operator fun invoke(naturalKey: UserNaturalKey) = User(naturalKey)

        // for java (MapStruct so on)
        @JvmStatic fun of(naturalKey: UserNaturalKey) = invoke(naturalKey)
        @JvmStatic fun of(naturalKey: String) = invoke(UserNaturalKey.of(naturalKey))
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(naturalKey: String) = of(naturalKey)
    }
}


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
