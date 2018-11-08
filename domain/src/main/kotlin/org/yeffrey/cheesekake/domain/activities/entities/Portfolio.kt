package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Either
import arrow.core.Option
import arrow.core.right
import arrow.core.toOption
import arrow.validation.validate
import org.yeffrey.cheesekake.domain.*

data class ActivityCreated(val id: Int, val title: String, val summary: String) : Event
data class ActivityDetailsCorrected(val id: Int, val title: String, val summary: String) : Event
data class ActivityResourceAdded(val id: Int, val resourceId: Int, val quantity: Int) : Event
data class ActivityResourceRemoved(val id: Int, val resourceId: Int) : Event

enum class TrainingLevel {
    Low,
    Normal,
    Hard
}

fun main(args: Array<String>) {
    val rn = ResourceName.from("")
    val rd = ResourceDescription.from("")
    val r = validate(rn, rd) { a, b ->
        Resource(1, a, b)
    }
    println(r)
}

data class Quantity internal constructor(val value: Int) {
    companion object {
        fun from(value: Int): Either<ValidationError, Quantity> = value.toQuantity(ValidationError.InvalidQuantity, ::Quantity)
    }
}

data class ResourceName internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ResourceName> = value.toDomainString(250, ValidationError.InvalidResourceName, ::ResourceName)
    }
}

data class ResourceDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ResourceDescription> = value.toDomainString(5000, ValidationError.InvalidResourceDescription, ::ResourceDescription)
    }
}

data class Resource constructor(val id: Int, val name: ResourceName, val description: ResourceDescription)

data class SkillName internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, SkillName> = value.toDomainString(250, ValidationError.InvalidSkillName, ::SkillName)
    }
}

data class SkillDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, SkillDescription> = value.toDomainString(5000, ValidationError.InvalidSkillDescription, ::SkillDescription)
    }
}

data class Skill constructor(val id: Int, val name: SkillName, val description: SkillDescription)

data class ActivityTitleTwo internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ActivityTitleTwo> = value.toDomainString(250, ValidationError.InvalidActivityTitle, ::ActivityTitleTwo)
    }
}

data class ActivitySummary internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ActivitySummary> = value.toDomainString(250, ValidationError.InvalidActivitySummary, ::ActivitySummary)
    }
}

data class ActivityDetailsMemento(val id: Int, val title: String, val summary: String)
data class ActivityDetails internal constructor(val id: Int, val title: ActivityTitleTwo, val summary: ActivitySummary) {
    companion object {
        private fun build(title: String, summary: String, block: (ActivityTitleTwo, ActivitySummary) -> ActivityDetails): Either<List<ValidationError>, ActivityDetails> {
            val t = ActivityTitleTwo.from(title)
            val s = ActivitySummary.from(summary)
            return validate(t, s, block)
        }

        fun from(memento: ActivityDetailsMemento): Option<ActivityDetails> {
            return ActivityDetails.build(memento.title, memento.summary) { title, summary ->
                ActivityDetails(memento.id, title, summary)
            }.toOption()
        }

        fun new(id: Int, title: String, summary: String): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityCreated>> {
            return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
                ActivityDetails(id, titleValue, summaryValue)
            }.map {
                CommandResult(it, ActivityCreated(it.id, it.title.value, it.summary.value))
            }
        }

    }

    fun updateActivityDetails(title: String, summary: String): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityDetailsCorrected>> {
        return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
            this.copy(title = titleValue, summary = summaryValue)
        }.map {
            CommandResult(it, ActivityDetailsCorrected(it.id, it.title.value, it.summary.value))
        }
    }
}

data class ActivityResource constructor(val resourceId: Int) {
    var qty: Quantity = Quantity(0)

    companion object {
        fun from(resourceId: Int, quantity: Int): Either<List<ValidationError>, ActivityResource> {
            val qty = quantity.toQuantity(ValidationError.InvalidQuantity, ::Quantity)
            return validate(qty, Either.right()) { validQty, _ ->
                val resource = ActivityResource(resourceId)
                resource.qty = validQty
                resource
            }
        }
    }
}

data class ActivityResourcesRequirementMemento(val id: Int, val resources: MutableSet<ActivityResource> = mutableSetOf())
data class ActivityResourcesRequirement internal constructor(val id: Int, val resources: Set<ActivityResource> = emptySet()) {
    companion object {
        fun from(memento: ActivityResourcesRequirementMemento): Option<ActivityResourcesRequirement> {
            return ActivityResourcesRequirement(memento.id, memento.resources.toSet()).toOption()
        }
    }

    fun add(resource: ActivityResource): Either<List<ValidationError>, CommandResult<ActivityResourcesRequirement, ActivityResourceAdded>> {
        val mutableResources = this.resources.toMutableSet()
        return when (mutableResources.add(resource)) {
            true -> Either.right(CommandResult(this.copy(resources = mutableResources.toSet()), ActivityResourceAdded(this.id, resource.resourceId, resource.qty.value)))
            false -> Either.left(listOf(ValidationError.DuplicateActivityResource))
        }
    }

    fun remove(resourceId: Int): Either<ValidationError, CommandResult<ActivityResourcesRequirement, ActivityResourceRemoved>> {
        val mutableResources = this.resources.toMutableSet()
        return when (mutableResources.removeIf { r: ActivityResource -> r.resourceId == resourceId }) {
            true -> Either.right(CommandResult(this.copy(resources = mutableResources.toSet()), ActivityResourceRemoved(this.id, resourceId)))
            false -> Either.left(ValidationError.DuplicateActivityResource)
        }
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


