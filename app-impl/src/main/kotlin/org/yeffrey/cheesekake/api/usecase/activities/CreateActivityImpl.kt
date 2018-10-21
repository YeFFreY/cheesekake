package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.ValidatedNel
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityBase
import org.yeffrey.cheesekake.domain.activities.entities.Writer

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun handle(request: CreateActivity.Request, presenter: CreateActivity.Presenter) = mustBeAuthenticated(request, presenter) {
        val newActivity = request.toDomain(Writer(it))
        when (newActivity) {
             is Valid -> {
                 val id = activityGateway.create(newActivity.a)
                 presenter.success(id)
             }
            is Invalid -> presenter.validationFailed(newActivity.e.all)
        }
    }
}

fun CreateActivity.Request.toDomain(writer: Writer): ValidatedNel<ValidationError, ActivityBase>  = ActivityBase.new(this.title, this.summary, writer)
