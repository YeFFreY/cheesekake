package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Option
import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.isNotBlankAndMaxLength
import org.yeffrey.cheesekake.domain.users.entities.UserId

data class Writer(val userId: UserId)
typealias ActivityId = Int


data class ActivityBase (val id: Option<ActivityId> = Option.empty(), val title: ActivityTitle, val summary: String, val writer: Writer) {
    companion object {
        fun new(title: String, summary: String, writer: Writer): ValidatedNel<ValidationError, ActivityBase> {
            return validate(title, summary) { t, s ->
                ActivityBase(title = t, summary = s, writer = writer)
            }
        }
    }
}

fun ActivityBase.update(title: String, summary: String):ValidatedNel<ValidationError, ActivityBase> {
    return validate(title, summary) { t, s -> this.copy(title = t, summary = s)
    }
}

fun ActivityBase.writtenBy(writer: Writer): Boolean  = this.writer == writer


data class ActivityTitle internal constructor(val value: String) {
    companion object {
        fun invalid(value: String): ActivityTitle  =ActivityTitle("Invalid : $value")
    }
}



fun String.activityTitle(): Validated<ValidationError, ActivityTitle> {
    return when {
        this.isNotBlankAndMaxLength(250) -> Valid(ActivityTitle(this))
        else -> Invalid(ValidationError.InvalidTitle)
    }
}

private fun validate(title: String, summary: String, block: (ActivityTitle, String ) -> ActivityBase):ValidatedNel<ValidationError, ActivityBase> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            title.activityTitle().toValidatedNel(),
            Valid(summary)
    ) {(title, summary) ->
        block(title, summary)
    }.fix()

}