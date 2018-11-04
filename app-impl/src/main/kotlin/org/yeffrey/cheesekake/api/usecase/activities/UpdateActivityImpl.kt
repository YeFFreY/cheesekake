package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.Result
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDescriptionUpdated
import org.yeffrey.cheesekake.domain.activities.entities.updateDescription

class UpdateActivityImpl(private val activityGateway: UpdateActivityGateway) : UpdateActivity {
    override suspend fun handle(request: UpdateActivity.Request, presenter: UpdateActivity.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        activityGateway.getDescription(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
            when (true) {
                false -> presenter.accessDenied()
                true -> process(request.toDomain(activity), presenter)
            }
        }
    }

    private suspend fun process(result: ValidatedNel<ValidationError, Result<Activity, ActivityDescriptionUpdated>>, presenter: UpdateActivity.Presenter) {
        when (result) {
            is Valid -> presenter.success(activityGateway.descriptionUpdated(result.a.event))
            is Invalid -> presenter.validationFailed(result.e.all)
        }
    }


}

fun UpdateActivity.Request.toDomain(activity: Activity): ValidatedNel<ValidationError, Result<Activity, ActivityDescriptionUpdated>> = activity.updateDescription(this.title, this.summary)