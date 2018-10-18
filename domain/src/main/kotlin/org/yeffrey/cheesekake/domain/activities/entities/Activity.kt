package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Option
import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.isNotBlankAndMaxLength


typealias ActivityId = Int
typealias AuthorId = Int

data class Activity(val id:Option<ActivityId>, val title: ActivityTitle, val summary: String, val authorId: AuthorId)



class ActivityTitle internal constructor(val value: String)

fun String.activityTitle() : ValidatedNel<ValidationError, ActivityTitle> {
    return when {
        this.isNotBlankAndMaxLength(250) -> Valid(ActivityTitle(this))
        else -> Invalid(ValidationError.InvalidTitle).toValidatedNel()
    }
}