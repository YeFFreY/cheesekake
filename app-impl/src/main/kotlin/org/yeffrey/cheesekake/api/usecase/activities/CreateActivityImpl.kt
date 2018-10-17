package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.command.NewActivity
import org.yeffrey.cheesekake.domain.activities.command.activityTitle

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun create(request: CreateActivity.Request, presenter: CreateActivity.Presenter) {
        if(!request.allow()) presenter.accessDenied()
        val newActivity = request.toDomain()
        when (newActivity) {
             is Valid -> {
                 val id = activityGateway.create(newActivity.a)
                 presenter.success(id)
             }
            is Invalid -> presenter.validationFailed(newActivity.e.all)
        }
    }
}

fun CreateActivity.Request.toDomain(): ValidatedNel<ValidationError, NewActivity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            this.title.activityTitle(),
            Valid(this.summary)
    ) {(title, summary) ->
        NewActivity(title, summary)
    }.fix()
}