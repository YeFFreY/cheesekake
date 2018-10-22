package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDescription
import org.yeffrey.cheesekake.domain.activities.entities.Writer
import org.yeffrey.cheesekake.domain.activities.entities.new

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun handle(request: CreateActivity.Request, presenter: CreateActivity.Presenter) = mustBeAuthenticated(request, presenter) {
        val newActivity = request.toDomain(Writer(it))
        when (newActivity) {
            is Valid -> presenter.success(activityGateway.create(newActivity.a))
            is Invalid -> presenter.validationFailed(newActivity.e.all)
        }
    }
}

fun CreateActivity.Request.toDomain(writer: Writer): ValidatedNel<ValidationError, ActivityDescription> = Activity.new(this.title, this.summary, writer)
