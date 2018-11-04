package org.yeffrey.cheesekake.domain

sealed class ValidationError(val message: String) {
    object InvalidTitle: ValidationError("ActivityBase title is invalid")
    object InvalidUsername: ValidationError("Username is invalid")
    object InvalidPassword: ValidationError("Password is invalid")
    object DuplicateActivityResource : ValidationError("Duplicate resource")
}

interface Event

data class Result<T, E : Event>(val result: T, val event: E)

