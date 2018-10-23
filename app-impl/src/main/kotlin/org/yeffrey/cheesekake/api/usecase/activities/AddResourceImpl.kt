package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.activities.AddResourcesActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Writer
import org.yeffrey.cheesekake.domain.activities.entities.writtenBy

class AddResourceImpl(private val activityGateway: AddResourcesActivityGateway) : AddResource {
    override suspend fun handle(request: AddResource.Request, presenter: AddResource.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        activityGateway.getResources(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
            when (activity.writtenBy(Writer(userId))) {
                false -> presenter.accessDenied()
                true -> process(request.toDomain(activity), presenter)
            }
        }
    }

    private suspend fun process(result: ActivityResources, presenter: AddResource.Presenter) {
        presenter.success(activityGateway.updateResources(result))
    }
}

fun AddResource.Request.toDomain(activityResources: ActivityResources): ActivityResources = activityResources.add(this.resources)

