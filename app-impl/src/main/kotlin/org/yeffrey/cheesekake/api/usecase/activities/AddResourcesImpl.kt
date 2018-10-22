package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.activities.AddResourcesActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResources
import org.yeffrey.cheesekake.domain.activities.entities.Writer
import org.yeffrey.cheesekake.domain.activities.entities.add
import org.yeffrey.cheesekake.domain.activities.entities.writtenBy

class AddResourcesImpl(private val activityGateway: AddResourcesActivityGateway) : AddResources {
    override suspend fun handle(request: AddResources.Request, presenter: AddResources.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        activityGateway.get(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
            when (activity.writtenBy(Writer(userId))) {
                false -> presenter.accessDenied()
                true -> process(request.toDomain(activity), presenter)
            }
        }
    }

    private suspend fun process(result: ActivityResources, presenter: AddResources.Presenter) {
        presenter.success(activityGateway.update(result))
    }
}

fun AddResources.Request.toDomain(activityResources: ActivityResources): ActivityResources = activityResources.add(this.resources)

