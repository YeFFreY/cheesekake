package org.yeffrey.cheesekake.domain.activities.entities

import arrow.data.*
import org.yeffrey.cheesekake.domain.*

data class ActivityCreatedTwo(val id: Int, val title: String, val summary: String) : Event
data class ActivityDetailsCorrected(val id: Int, val title: String, val summary: String) : Event
data class ActivityResourceAddedTwo(val id: Int, val resourceId: Int, val qty: Int) : Event

enum class TrainingLevel {
    Low,
    Normal,
    Hard
}

data class Quantity internal constructor(val value: Int) {
    companion object {
        fun from(value: Int): ValidatedNel<ValidationError, Quantity> = value.toQuantity(ValidationError.InvalidQuantity, ::Quantity)
    }
}

data class ResourceName internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ResourceName> = value.toDomainString(250, ValidationError.InvalidResourceName, ::ResourceName)
    }
}

data class ResourceDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ResourceDescription> = value.toDomainString(5000, ValidationError.InvalidResourceDescription, ::ResourceDescription)
    }
}

data class Resource constructor(val id: Int, val name: ResourceName, val description: ResourceDescription)

data class SkillName internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, SkillName> = value.toDomainString(250, ValidationError.InvalidSkillName, ::SkillName)
    }
}

data class SkillDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, SkillDescription> = value.toDomainString(5000, ValidationError.InvalidSkillDescription, ::SkillDescription)
    }
}

data class Skill constructor(val id: Int, val name: SkillName, val description: SkillDescription)

data class ActivityTitleTwo internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ActivityTitleTwo> = value.toDomainString(250, ValidationError.InvalidActivityTitle, ::ActivityTitleTwo)
    }
}

data class ActivitySummary internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ActivitySummary> = value.toDomainString(250, ValidationError.InvalidActivitySummary, ::ActivitySummary)
    }
}

data class ActivityDetails internal constructor(val id: Int, val title: ActivityTitleTwo, val summary: ActivitySummary) {
    companion object {
        fun build(title: String, summary: String, block: (ActivityTitleTwo, ActivitySummary) -> ActivityDetails): ValidatedNel<ValidationError, ActivityDetails> {
            return Validated.applicative(Nel.semigroup<ValidationError>()).map(
                    ActivityTitleTwo.from(title),
                    ActivitySummary.from(summary)
            ) { (titleValue, summaryValue) ->
                block(titleValue, summaryValue)
            }.fix()
        }

        fun new(id: Int, title: String, summary: String): ValidatedNel<ValidationError, CommandResult<ActivityDetails, ActivityCreatedTwo>> {
            return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
                ActivityDetails(id, titleValue, summaryValue)
            }.map {
                CommandResult(it, ActivityCreatedTwo(it.id, it.title.value, it.summary.value))
            }
        }
    }

    fun updateActivityDetails(activityDetails: ActivityDetails, title: String, summary: String): ValidatedNel<ValidationError, CommandResult<ActivityDetails, ActivityDetailsCorrected>> {
        return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
            activityDetails.copy(title = titleValue, summary = summaryValue)
        }.map {
            CommandResult(it, ActivityDetailsCorrected(it.id, it.title.value, it.summary.value))
        }
    }
}

data class ActivityResource constructor(val resourceId: Int) {
    val qty: Quantity = Quantity(0)

    companion object {
        fun from(resourceId: Int, quantity: Int): ValidatedNel<ValidationError, ActivityResource> = quantity.toQuantity(ValidationError.InvalidQuantity, ::Quantity).map { ActivityResource(resourceId) }
    }
}

data class ActivityResourcesRequirement constructor(val id: Int, val resources: Set<ActivityResource> = emptySet()) {
    fun add(resource: ActivityResource): ValidatedNel<ValidationError, CommandResult<ActivityResourcesRequirement, ActivityResourceAddedTwo>> {
        val mutableResources = this.resources.toMutableSet()
        if (!mutableResources.add(resource)) {
            return Invalid(ValidationError.DuplicateActivityResource).toValidatedNel()
        }
        return Valid(CommandResult(this.copy(resources = mutableResources.toSet()), ActivityResourceAddedTwo(this.id, resource.resourceId, resource.qty.value)))
    }
}

data class TrainedSkill constructor(val skillId: Int) {
    val level = TrainingLevel.Normal
}

data class ActivityTrainedSkills constructor(val id: Int, val skills: Set<TrainedSkill> = emptySet())

data class RequiredSkill constructor(val skillId: Int) {
    val level = TrainingLevel.Normal
}

data class ActivitySkillsRequirement(val id: Int, val skills: Set<RequiredSkill> = emptySet())


