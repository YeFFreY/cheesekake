package org.yeffrey.cheesekake.domain.users.command

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.isMinLength
import org.yeffrey.cheesekake.domain.isNotBlankAndMaxLength

class Username internal constructor(val value: String)
class Password internal constructor(val value: String)

fun String.toUsername() : ValidatedNel<ValidationError, Username> {
    return when {
        this.isNotBlankAndMaxLength(400) -> Valid(Username(this))
        else -> Invalid(ValidationError.InvalidUsername).toValidatedNel()
    }
}

fun String.toPassword() : ValidatedNel<ValidationError, Password> {
    return when {
        validPassword(this) -> Valid(Password(this))
        else -> Invalid(ValidationError.InvalidPassword).toValidatedNel()
    }
}

private fun validPassword(value: String) : Boolean {
    return value.isNotBlankAndMaxLength(72) && value.isMinLength(10)
}


data class Registration(val username: Username, val password: Password)