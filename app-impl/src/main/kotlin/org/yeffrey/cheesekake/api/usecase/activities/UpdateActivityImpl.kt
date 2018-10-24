package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.Writer
import org.yeffrey.cheesekake.domain.activities.entities.updateDescription
import org.yeffrey.cheesekake.domain.activities.entities.writtenBy

class UpdateActivityImpl(private val activityGateway: UpdateActivityGateway) : UpdateActivity {
    override suspend fun handle(request: UpdateActivity.Request, presenter: UpdateActivity.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        activityGateway.getDescription(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
            when (activity.writtenBy(Writer(userId))) {
                false -> presenter.accessDenied()
                true -> process(request.toDomain(activity), presenter)
            }
        }
    }

    private suspend fun process(result: ValidatedNel<ValidationError, Activity>, presenter: UpdateActivity.Presenter) {
        when (result) {
            is Valid -> {
                result.a.publishedEvents().forEach {
                    activityGateway.descriptionUpdated(it)
                }
                presenter.success(activityGateway.descriptionUpdated())
            }
            is Invalid -> presenter.validationFailed(result.e.all)
        }
    }


}

fun UpdateActivity.Request.toDomain(activity: Activity): ValidatedNel<ValidationError, Activity> = activity.updateDescription(this.title, this.summary)