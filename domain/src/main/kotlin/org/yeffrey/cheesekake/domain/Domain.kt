package org.yeffrey.cheesekake.domain

import arrow.core.Either
import org.yeffrey.core.error.ErrorDescription
import org.yeffrey.core.utils.isMaxLength

sealed class ValidationError(override val message: String) : ErrorDescription {
    object InvalidActivityTitle : ValidationError("Activity title must not be blank and max 250 chars")
    object InvalidActivitySummary : ValidationError("Activity summary must not be blank and max 250 chars")
    object InvalidResourceName : ValidationError("Resource name must not be blank and max 250 chars")
    object InvalidResourceDescription : ValidationError("Resource description must not be blank and max 5000 chars")
    object InvalidSkillName : ValidationError("Skill name must not be blank and max 250 chars")
    object InvalidSkillDescription : ValidationError("Skill description must not be blank and max 5000 chars")
    object InvalidUsername: ValidationError("Username is invalid")
    object InvalidPassword: ValidationError("Password is invalid")
    object DuplicateActivityResource : ValidationError("Duplicate resource")
    object UnknownActivityResource : ValidationError("Unknown resource")
    object InvalidQuantity : ValidationError("Invalid value for Quantity")
}

interface Event
data class CommandResult<T, E : Event>(val result: T, val event: E)

fun String.isDomainString(maxLength: Int) = this.isMaxLength(maxLength) && this.isNotBlank()
fun <T> String.toDomainString(maxLength: Int, validationError: ValidationError, block: (s: String) -> T): Either<ValidationError, T> {
    return when (this.isDomainString(maxLength)) {
        true -> Either.right((block(this)))
        else -> Either.left(validationError)
    }
}

fun Int.isQuantity() = this >= 0
fun <T> Int.toQuantity(validationError: ValidationError, block: (s: Int) -> T): Either<ValidationError, T> {
    return when (this.isQuantity()) {
        true -> Either.right(block(this))
        else -> Either.left(validationError)
    }
}
