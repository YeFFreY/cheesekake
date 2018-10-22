package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Option
import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.isNotBlankAndMaxLength
import org.yeffrey.cheesekake.domain.users.entities.UserId

data class Writer(val userId: UserId)
typealias ActivityId = Int
typealias ResourceId = Int
typealias SkillId = Int

abstract class Activity(open val id: Option<ActivityId>, open val writer: Writer) {
    companion object
}

data class ActivityDescription(override val id: Option<ActivityId> = Option.empty(), override val writer: Writer, val title: ActivityTitle, val summary: String) : Activity(id, writer)
data class ActivityResources(override val id: Option<ActivityId>, override val writer: Writer, val resources: Set<ResourceId>) : Activity(id, writer)
data class ActivitySkills(override val id: Option<ActivityId>, override val writer: Writer, val resources: Set<SkillId>) : Activity(id, writer)


fun Activity.Companion.new(title: String, summary: String, writer: Writer): ValidatedNel<ValidationError, ActivityDescription> {
    return validate(title, summary) { t, s ->
        ActivityDescription(title = t, summary = s, writer = writer)
    }
}

fun ActivityDescription.update(title: String, summary: String): ValidatedNel<ValidationError, ActivityDescription> = validate(title, summary) { t, s -> this.copy(title = t, summary = s) }

fun ActivityResources.add(resources: List<ResourceId>): ActivityResources {
    val newResources = this.resources.plus(resources)
    return this.copy(resources = newResources)
}


fun Activity.writtenBy(writer: Writer): Boolean = this.writer == writer


data class ActivityTitle internal constructor(val value: String) {
    companion object {
        fun invalid(value: String): ActivityTitle = ActivityTitle("Invalid : $value")
    }
}


fun String.activityTitle(): Validated<ValidationError, ActivityTitle> {
    return when {
        this.isNotBlankAndMaxLength(250) -> Valid(ActivityTitle(this))
        else -> Invalid(ValidationError.InvalidTitle)
    }
}

private fun validate(title: String, summary: String, block: (ActivityTitle, String) -> ActivityDescription): ValidatedNel<ValidationError, ActivityDescription> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            title.activityTitle().toValidatedNel(),
            Valid(summary)
    ) { (title, summary) ->
        block(title, summary)
    }.fix()

}