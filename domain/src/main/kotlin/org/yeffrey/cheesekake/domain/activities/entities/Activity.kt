package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Option
import arrow.data.*
import org.yeffrey.cheesekake.domain.*
import org.yeffrey.cheesekake.domain.users.entities.UserId

data class Writer(val userId: UserId)
typealias ActivityId = Int
typealias ResourceId = Int
typealias SkillId = Int

data class ActivityCreated(val title: String, val summary: String, val authorId: Int) : Event
data class ActivityDescriptionUpdated(val id: Int, val title: String, val summary: String, val authorId: Int) : Event
data class ActivityResourceAdded(val id: Int, val resourceId: Int, val authorId: Int) : Event

data class Activity internal constructor(
        val id: ActivityId,
        val writer: Writer,
        val description: ActivityDescription,
        val resources: Set<ResourceId> = emptySet(),
        val skills: Set<SkillId> = emptySet()) : Aggregate() {
    companion object {
        fun new(id: ActivityId, title: String, summary: String, writer: Writer): ValidatedNel<ValidationError, Activity> {
            return validate(title, summary) { t, s ->
                val activity = Activity(id, writer, ActivityDescription(t, s))
                activity.eventHolder.publish(ActivityCreated(activity.description.title.value, activity.description.summary, activity.writer.userId))
                activity
            }
        }

        fun from(memento: ActivityMemento): Option<Activity> {
            return ActivityTitle.from(memento.title).map {
                ActivityDescription(it, memento.summary)
            }.map { description ->
                Activity(memento.id, Writer(memento.authorId), description)
            }.toOption()
        }
    }
}

data class ActivityMemento(val id: Int, val authorId: Int, val title: String, val summary: String, val resources: Set<Int> = emptySet(), val skills: Set<SkillId> = emptySet())

data class ActivityDescription(val title: ActivityTitle, val summary: String)


fun Activity.updateDescription(title: String, summary: String): ValidatedNel<ValidationError, Result<Activity, ActivityDescriptionUpdated>> {
    return validate(title, summary) { t, s ->
        this.copy(description = ActivityDescription(title = t, summary = s))
    }.map { activity ->
        Result(activity, ActivityDescriptionUpdated(this.id, activity.description.title.value, activity.description.summary, activity.writer.userId))
    }
}

fun Activity.add(resourceId: ResourceId): Activity {
    val newResources = this.resources.plus(resourceId)
    val activity = this.copy(resources = newResources)
    activity.eventHolder.publish(ActivityResourceAdded(activity.id, resourceId, activity.writer.userId))
    return activity
}


fun Activity.writtenBy(writer: Writer): Boolean = this.writer == writer

data class ActivityTitle internal constructor(val value: String) {
    companion object {
        fun from(value: String): Validated<ValidationError, ActivityTitle> {
            return when (ActivityTitle.isValid(value)) {
                true -> Valid(ActivityTitle(value))
                else -> Invalid(ValidationError.InvalidTitle)
            }
        }

        fun isValid(value: String): Boolean = value.isNotBlankAndMaxLength(250)
    }
}

private fun validate(title: String, summary: String, block: (ActivityTitle, String) -> Activity): ValidatedNel<ValidationError, Activity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            ActivityTitle.from(title).toValidatedNel(),
            Valid(summary)
    ) { (title, summary) ->
        block(title, summary)
    }.fix()

}