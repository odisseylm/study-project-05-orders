package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe
import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable


@Untainted @Immutable
class Email private constructor (@param:Tainted @field:Untainted val value: String) {
    init { validateEmail(value) }
    @Untainted
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as Email).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        operator fun invoke(email: String) = Email(email)

        // for Java (MapStruct so on)
        @JvmStatic fun of(email: String) = invoke(email)
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(email: String) = invoke(email)
    }
}


@Untainted @Immutable
class Phone private constructor (@param:Tainted @field:Untainted val value: String) {
    init { validatePhone(value) }
    @Untainted
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as Phone).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        operator fun invoke(email: String) = Phone(email)

        // for java (MapStruct, so on)
        @JvmStatic fun of(email: String) = invoke(email)
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(email: String) = invoke(email)
    }
}


// see https://www.baeldung.com/java-email-validation-regex
// RFC5322
//private val emailRfc5322Pattern = Regex("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
// Strict Regular Expression Validation
//private val emailStrictPattern = Regex("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
//        + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
private val emailOwaspPattern = Regex("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
private fun validateEmail(email: CharSequence?) {
    check(!email.isNullOrBlank()) { "Email cannot be null/blank." }
    if (!emailOwaspPattern.matches(email)) throw IllegalArgumentException("Invalid email [${email.safe}].")
}


// see https://www.baeldung.com/java-regex-validate-phone-numbers
//private val phonePattern = Regex("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$")
//private val phonePattern = Regex("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$")
private val phonePattern = Regex("^\\+?[1-9][0-9]{7,14}$")
private fun validatePhone(email: CharSequence?) {
    check(!email.isNullOrBlank()) { "Phone number cannot be null/blank." }
    if (!phonePattern.matches(email)) throw IllegalArgumentException("Invalid phone number [${email.safe}].")
}


/*
private const val MAX_ACCOUNT_LENGTH = 50
private val ACCOUNT_ID_PATTERN = Regex("^[a-zA-Z0-9]*$")

private fun validateAccountId(accountId: String) {
    val isValid = accountId.isNotBlank()
            && accountId.length <= MAX_ACCOUNT_LENGTH
            && accountId.matches(ACCOUNT_ID_PATTERN)

    if (!isValid) {
        throw InvalidArgumentException(
            "Account ID ${accountId.safe} has wrong format.",
            accountId,
            "Account ID should be string of chars/digits with 1-${MAX_ACCOUNT_LENGTH} length.")
    }
}
*/
