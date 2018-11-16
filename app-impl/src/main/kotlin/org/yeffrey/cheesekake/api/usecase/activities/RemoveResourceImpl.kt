package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Either
import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.RemoveResourceActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResourceRemoved
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResourcesRequirement
import org.yeffrey.cheesekake.domain.activities.isAuthor
import org.yeffrey.cheesekake.domain.respect

class RemoveResourceImpl(private val activityGateway: RemoveResourceActivityGateway) : RemoveResource {
    override suspend fun handle(request: RemoveResource.Request, presenter: RemoveResource.Presenter, userId: Option<Int>) = mustBeAuthenticated(userId, presenter) { theUserId ->
        activityGateway.getResources(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
            when (respect(theUserId, activity, ::isAuthor)) {
                false -> presenter.accessDenied()
                true -> process(request.toDomain(activity), presenter)
            }
        }
    }

    private suspend fun process(result: Either<List<ValidationError>, CommandResult<ActivityResourcesRequirement, ActivityResourceRemoved>>, presenter: RemoveResource.Presenter) {
        when (result) {
            is Either.Left -> presenter.validationFailed(result.a)
            is Either.Right -> {
                activityGateway.resourceRemoved(result.b.event)
                presenter.success()
            }
        }
    }

}

fun RemoveResource.Request.toDomain(activity: ActivityResourcesRequirement): Either<List<ValidationError>, CommandResult<ActivityResourcesRequirement, ActivityResourceRemoved>> {
    return activity.remove(this.resourceId)
}
