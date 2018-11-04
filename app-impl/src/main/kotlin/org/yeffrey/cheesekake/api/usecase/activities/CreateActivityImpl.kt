package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.Result
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.ActivityCreated
import org.yeffrey.cheesekake.domain.activities.entities.ActivityId

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun handle(request: CreateActivity.Request, presenter: CreateActivity.Presenter) = mustBeAuthenticated(request, presenter) {
        val newActivityId = activityGateway.nextIdentity()
        val newActivity = request.toDomain(newActivityId)
        when (newActivity) {
            is Valid -> presenter.success(activityGateway.activityCreated(newActivity.a.event))
            is Invalid -> presenter.validationFailed(newActivity.e.all)
        }
    }
}

fun CreateActivity.Request.toDomain(newId: ActivityId): ValidatedNel<ValidationError, Result<Activity, ActivityCreated>> = Activity.new(newId, this.title, this.summary)
