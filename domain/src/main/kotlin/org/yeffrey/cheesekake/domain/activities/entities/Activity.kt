package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Option
import arrow.core.toOption
import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.isNotBlankAndMaxLength


typealias ActivityId = Int
typealias AuthorId = Int

class Activity private constructor(var title: ActivityTitle, var summary: String, val authorId: AuthorId) {
    var id:Option<ActivityId> = Option.empty()
        private set

    companion object {
        fun new(title: ActivityTitle, summary: String, authorId: AuthorId) : Activity {
            return Activity(title, summary, authorId)
        }
        fun from(id: ActivityId, title: ActivityTitle, summary: String, authorId: AuthorId) : Activity {
            val activity = Activity(title,summary, authorId)
            activity.id = id.toOption()
            return activity
        }
    }

    fun update(title: ActivityTitle, summary: String) : Activity {
        this.title = title
        this.summary = summary
        return this
    }
}



class ActivityTitle internal constructor(val value: String) {
    companion object {
        fun invalid(value: String): ActivityTitle {
            return ActivityTitle(value)
        }
    }
}


fun String.activityTitle() : ValidatedNel<ValidationError, ActivityTitle> {
    return when {
        this.isNotBlankAndMaxLength(250) -> Valid(ActivityTitle(this))
        else -> Invalid(ValidationError.InvalidTitle).toValidatedNel()
    }
}