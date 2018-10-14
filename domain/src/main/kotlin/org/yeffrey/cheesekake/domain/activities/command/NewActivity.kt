package org.yeffrey.cheesekake.domain.activities.command

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.isNotBlankAndMaxLength

class ActivityTitle internal constructor(val value: String)

fun String.activityTitle() : ValidatedNel<ValidationError, ActivityTitle> {
    return when {
        this.isNotBlankAndMaxLength(250) -> Valid(ActivityTitle(this))
        else -> Invalid(ValidationError.InvalidTitle).toValidatedNel()
    }
}

data class NewActivity(val title: ActivityTitle, val summary: String)