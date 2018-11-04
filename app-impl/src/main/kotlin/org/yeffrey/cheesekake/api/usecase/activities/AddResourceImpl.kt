package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.Result
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.AddResourcesActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResourceAdded
import org.yeffrey.cheesekake.domain.activities.entities.add

class AddResourceImpl(private val activityGateway: AddResourcesActivityGateway) : AddResource {
    override suspend fun handle(request: AddResource.Request, presenter: AddResource.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        if (activityGateway.exists(request.resourceId)) {
            activityGateway.getResources(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
                when (true) {
                    false -> presenter.accessDenied()
                    true -> process(request.toDomain(activity), presenter)
                }
            }
        } else {
            presenter.notFound(request.resourceId)
        }
    }

    private suspend fun process(result: ValidatedNel<ValidationError, Result<Activity, ActivityResourceAdded>>, presenter: AddResource.Presenter) {
        when (result) {
            is Valid -> presenter.success(activityGateway.resourceAdded(result.a.event))
            is Invalid -> presenter.validationFailed(result.e.all)
        }
    }
}

fun AddResource.Request.toDomain(activity: Activity): ValidatedNel<ValidationError, Result<Activity, ActivityResourceAdded>> = activity.add(this.resourceId)

