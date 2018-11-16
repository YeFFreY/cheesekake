package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Either
import arrow.core.Option
import arrow.core.right
import arrow.core.toOption
import arrow.validation.validate
import org.yeffrey.cheesekake.domain.*
import org.yeffrey.cheesekake.domain.activities.Activity

data class ActivityCreated(val id: Int, val title: String, val summary: String, val authorId: Int) : Event
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

data class ActivityResourcesRequirementMemento(val id: Int, val resources: MutableSet<ActivityResource> = mutableSetOf(), val authorId: Int)
data class ActivityResourcesRequirement internal constructor(val id: Int, val resources: Set<ActivityResource> = emptySet(), override val authorId: Int) : Activity {
    companion object {
        fun from(memento: ActivityResourcesRequirementMemento): Option<ActivityResourcesRequirement> {
            return ActivityResourcesRequirement(memento.id, memento.resources.toSet(), memento.authorId).toOption()
        }
    }

    fun add(resource: ActivityResource): Either<List<ValidationError>, CommandResult<ActivityResourcesRequirement, ActivityResourceAdded>> {
        val mutableResources = this.resources.toMutableSet()
        return when (mutableResources.add(resource)) {
            true -> Either.right(CommandResult(this.copy(resources = mutableResources.toSet()), ActivityResourceAdded(this.id, resource.resourceId, resource.qty.value)))
            false -> Either.left(listOf(ValidationError.DuplicateActivityResource))
        }
    }

    fun remove(resourceId: Int): Either<List<ValidationError>, CommandResult<ActivityResourcesRequirement, ActivityResourceRemoved>> {
        val mutableResources = this.resources.toMutableSet()
        return when (mutableResources.removeIf { r: ActivityResource -> r.resourceId == resourceId }) {
            true -> Either.right(CommandResult(this.copy(resources = mutableResources.toSet()), ActivityResourceRemoved(this.id, resourceId)))
            false -> Either.left(listOf(ValidationError.UnknownActivityResource))
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


