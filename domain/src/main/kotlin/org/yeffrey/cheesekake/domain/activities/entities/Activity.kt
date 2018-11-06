package org.yeffrey.cheesekake.domain.activities.entities


import arrow.core.Option
import arrow.data.*
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.Event
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.core.utils.isMaxLength

typealias ActivityId = Int
typealias ResourceId = Int // Resource entity should be part of a user "Catalog" or "portfolio" to constraint the use of own resources, skills so that he cannot use resource from somebody else
typealias SkillId = Int

data class ActivityCreated(val id: ActivityId, val title: String, val summary: String) : Event
data class ActivityDescriptionUpdated(val id: Int, val title: String, val summary: String) : Event
data class ActivityResourceAdded(val id: Int, val resourceId: Int) : Event

data class Activity internal constructor(
        val id: ActivityId,
        val description: ActivityDescription,
        val resources: Set<ResourceId> = emptySet(),
        val skills: Set<SkillId> = emptySet()) {
    companion object {
        fun new(id: ActivityId, title: String, summary: String): ValidatedNel<ValidationError, CommandResult<Activity, ActivityCreated>> {
            return validate(title, summary) { t, s ->
                Activity(id, ActivityDescription(t, s))
            }.map { activity ->
                CommandResult(activity, ActivityCreated(activity.id, activity.description.title.value, activity.description.summary))
            }
        }

        fun from(memento: ActivityMemento): Option<Activity> {
            return ActivityTitle.from(memento.title).map {
                ActivityDescription(it, memento.summary)
            }.map { description ->
                Activity(memento.id, description, memento.resources, memento.skills)
            }.toOption()
        }
    }
}

data class ActivityMemento(val id: Int, val title: String, val summary: String, val resources: Set<Int> = emptySet(), val skills: Set<SkillId> = emptySet())

data class ActivityDescription(val title: ActivityTitleTwo, val summary: String)


fun Activity.updateDescription(title: String, summary: String): ValidatedNel<ValidationError, CommandResult<Activity, ActivityDescriptionUpdated>> {
    return validate(title, summary) { t, s ->
        this.copy(description = ActivityDescription(title = t, summary = s))
    }.map { activity ->
        CommandResult(activity, ActivityDescriptionUpdated(this.id, activity.description.title.value, activity.description.summary))
    }
}

fun Activity.add(resourceId: ResourceId): ValidatedNel<ValidationError, CommandResult<Activity, ActivityResourceAdded>> {
    return when (this.resources.contains(resourceId)) {
        true -> Invalid(ValidationError.DuplicateActivityResource).toValidatedNel()
        false -> {
            val newResources = this.resources.plus(resourceId)
            val activity = this.copy(resources = newResources)
            Valid(CommandResult(activity, ActivityResourceAdded(activity.id, resourceId)))
        }
    }
}


data class ActivityTitle internal constructor(val value: String) {
    companion object {
        fun from(value: String): Validated<ValidationError, ActivityTitleTwo> {
            return when (ActivityTitle.isValid(value)) {
                true -> Valid(ActivityTitleTwo(value))
                else -> Invalid(ValidationError.InvalidActivityTitle)
            }
        }

        fun isValid(value: String): Boolean = value.isMaxLength(250) && value.isNotBlank()
    }
}

private fun validate(title: String, summary: String, block: (ActivityTitleTwo, String) -> Activity): ValidatedNel<ValidationError, Activity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            ActivityTitle.from(title).toValidatedNel(),
            Valid(summary)
    ) { (title, summary) ->
        block(title, summary)
    }.fix()

}
