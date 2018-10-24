package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.ActivityId
import org.yeffrey.cheesekake.domain.activities.entities.Writer

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun handle(request: CreateActivity.Request, presenter: CreateActivity.Presenter) = mustBeAuthenticated(request, presenter) {
        val newActivityId = activityGateway.nextIdentity()
        val newActivity = request.toDomain(newActivityId, Writer(it))
        when (newActivity) {
            is Valid -> presenter.success(activityGateway.activityCreated(newActivity.a))
            is Invalid -> presenter.validationFailed(newActivity.e.all)
        }
    }
}

fun CreateActivity.Request.toDomain(newId: ActivityId, writer: Writer): ValidatedNel<ValidationError, Activity> = Activity.new(newId, this.title, this.summary, writer)
