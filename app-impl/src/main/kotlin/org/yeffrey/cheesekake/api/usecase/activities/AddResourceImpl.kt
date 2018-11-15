package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Either
import arrow.core.flatMap
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.AddResourcesActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResource
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResourceAdded
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResourcesRequirement
import org.yeffrey.cheesekake.domain.activities.isAuthor
import org.yeffrey.cheesekake.domain.respect

class AddResourceImpl(private val activityGateway: AddResourcesActivityGateway) : AddResource {
    override suspend fun handle(request: AddResource.Request, presenter: AddResource.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        if (activityGateway.exists(request.resourceId)) {
            activityGateway.getResources(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
                when (respect(userId, activity, ::isAuthor)) {
                    false -> presenter.accessDenied()
                    true -> process(request.toDomain(activity), presenter)
                }
            }
        } else {
            presenter.notFound(request.resourceId)
        }
    }

    private suspend fun process(result: Either<List<ValidationError>, CommandResult<ActivityResourcesRequirement, ActivityResourceAdded>>, presenter: AddResource.Presenter) {
        when (result) {
            is Either.Left -> presenter.validationFailed(result.a)
            is Either.Right -> presenter.success(activityGateway.resourceAdded(result.b.event))
        }
    }
}

fun AddResource.Request.toDomain(activity: ActivityResourcesRequirement): Either<List<ValidationError>, CommandResult<ActivityResourcesRequirement, ActivityResourceAdded>> {
    return ActivityResource.from(this.resourceId, this.quantity).flatMap {
        activity.add(it)
    }
}


