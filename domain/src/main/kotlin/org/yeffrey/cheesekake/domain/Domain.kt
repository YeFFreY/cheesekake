package org.yeffrey.cheesekake.domain

fun String.isNotBlankAndMaxLength(maxLength: Int): Boolean {
    return this.isNotBlank() && this.length <= maxLength
}

fun String.isMinLength(minLength: Int): Boolean {
    return this.isNotBlank() && this.filter { it != ' ' }.length >= minLength
}

sealed class ValidationError(val message: String) {
    object InvalidTitle: ValidationError("ActivityBase title is invalid")
    object InvalidUsername: ValidationError("Username is invalid")
    object InvalidPassword: ValidationError("Password is invalid")
}

sealed class EntityId {
    object None : EntityId()
    data class Some(val value: Int) : EntityId()
}