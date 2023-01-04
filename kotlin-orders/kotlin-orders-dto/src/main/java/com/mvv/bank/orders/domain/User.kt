package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe


class Email (val value: String) {
    init { validateEmail(value) }
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as Email).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        @JvmStatic
        fun of(email: String) = Email(email)
        @JvmStatic
        fun valueOf(email: String) = of(email)
    }
}


class Phone (val value: String) {
    init { validatePhone(value) }
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as Phone).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        @JvmStatic
        fun of(email: String) = Phone(email)
        @JvmStatic
        fun valueOf(email: String) = of(email)
    }
}


// it is not clear now what it should be??? phone and email can be changed? or cannot?
class UserNaturalKey (val value: String) {
    init {
        // now we will use email but later will change it
        Email(value) // used for validation only
    }
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as UserNaturalKey).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        @JvmStatic
        fun of(userNaturalKey: String) = UserNaturalKey(userNaturalKey)
        @JvmStatic
        fun valueOf(userNaturalKey: String) = of(userNaturalKey)
    }
}


// T O D O: think about natural-key
class User (val naturalKey: UserNaturalKey) {
    val value: String get() = naturalKey.value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as User).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        @JvmStatic
        fun of(naturalKey: UserNaturalKey) = User(naturalKey)
        @JvmStatic
        fun of(naturalKey: String) = User(UserNaturalKey(naturalKey))

        @JvmStatic
        fun valueOf(naturalKey: String) = of(naturalKey)
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


private const val MAX_CLIENT_LENGTH = 50
private val CLIENT_ID_PATTERN = Regex("^[a-zA-Z0-9]*\$")

private fun validateClient(clientId: String) {
    val isValid = clientId.isNotBlank()
            && clientId.length <= MAX_CLIENT_LENGTH
            && clientId.matches(CLIENT_ID_PATTERN)

    if (!isValid) {
        throw InvalidArgumentException(
            "Client ID ${clientId.safe} has wrong format.",
            clientId,
            "Client ID should be string of chars/digits with 1-${MAX_CLIENT_LENGTH} length.")
    }
}



private const val CURRENCY_LENGTH = 3
private val CURRENCY_PATTERN = Regex("^[A-Z]*\$")

private fun validateCurrency(currency: String) {
    val isValid = currency.isNotBlank()
            && currency.length == CURRENCY_LENGTH
            && currency.matches(CURRENCY_PATTERN)

    if (!isValid) {
        throw InvalidArgumentException(
            "Currency ${currency.safe} has wrong format.",
            currency,
            "Currency should be string of $CURRENCY_LENGTH chars.")
    }
}
*/