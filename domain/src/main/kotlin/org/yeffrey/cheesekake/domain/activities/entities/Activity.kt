package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.data.*
import org.yeffrey.cheesekake.domain.Aggregate
import org.yeffrey.cheesekake.domain.Event
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.isNotBlankAndMaxLength
import org.yeffrey.cheesekake.domain.users.entities.UserId

data class Writer(val userId: UserId)
typealias ActivityId = Int
typealias ResourceId = Int
typealias SkillId = Int

data class ActivityCreated(val title: String, val summary: String, val authorId: Int) : Event
data class ActivityDescriptionUpdated(val id: Int, val title: String, val summary: String, val authorId: Int) : Event
data class ActivityResourceAdded(val id: Int, val resourceId: Int, val authorId: Int) : Event

data class Activity constructor(
        val id: Option<ActivityId> = Option.empty(),
        val writer: Writer,
        val description: ActivityDescription,
        val resources: Set<ResourceId> = emptySet(),
        val skills: Set<SkillId> = emptySet()) : Aggregate() {
    companion object
}

data class ActivityDescription(val title: ActivityTitle, val summary: String)

fun Activity.Companion.new(title: String, summary: String, writer: Writer): ValidatedNel<ValidationError, Activity> {
    return validate(title, summary) { t, s ->
        val activity = Activity(writer = writer, description = ActivityDescription(t, s))
        activity.eventHolder.publish(ActivityCreated(activity.description.title.value, activity.description.summary, activity.writer.userId))
        activity
    }
}

fun Activity.updateDescription(title: String, summary: String): ValidatedNel<ValidationError, Activity> {
    return validate(title, summary) { t, s ->
        val activity = this.copy(description = ActivityDescription(title = t, summary = s))
        when (activity.id) {
            is None -> Unit
            is Some -> activity.eventHolder.publish(ActivityDescriptionUpdated(activity.id.t, activity.description.title.value, activity.description.summary, activity.writer.userId))
        }
        activity
    }
}

fun Activity.add(resourceId: ResourceId): Activity {
    val newResources = this.resources.plus(resourceId)
    val activity = this.copy(resources = newResources)
    when (activity.id) {
        is None -> Unit
        is Some -> activity.eventHolder.publish(ActivityResourceAdded(activity.id.t, resourceId, activity.writer.userId))
    }
    return activity
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

private fun validate(title: String, summary: String, block: (ActivityTitle, String) -> Activity): ValidatedNel<ValidationError, Activity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            title.activityTitle().toValidatedNel(),
            Valid(summary)
    ) { (title, summary) ->
        block(title, summary)
    }.fix()

}