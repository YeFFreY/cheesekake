package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Either
import arrow.core.Option
import arrow.core.right
import arrow.core.toOption
import arrow.validation.validate
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.Activity
import org.yeffrey.cheesekake.domain.toQuantity

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
            true -> Either.right(CommandResult(this.copy(resources = mutableResources.toSet()), ActivityResourceAdded(this.id, resource.resourceId, resource.quantity.value)))
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

data class ActivityResource constructor(val resourceId: Int, val quantity: Quantity) {

    companion object {
        fun from(resourceId: Int, quantity: Int): Either<List<ValidationError>, ActivityResource> {
            val qty = quantity.toQuantity(ValidationError.InvalidQuantity, ::Quantity)
            return validate(qty, right()) { validQty, _ ->
                ActivityResource(resourceId, validQty)
            }
        }
    }
}

data class Quantity internal constructor(val value: Int) {
    companion object {
        fun from(value: Int): Either<ValidationError, Quantity> = value.toQuantity(ValidationError.InvalidQuantity, ::Quantity)
    }
}