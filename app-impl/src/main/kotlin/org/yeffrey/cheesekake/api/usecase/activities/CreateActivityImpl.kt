package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.activityTitle

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun handle(request: CreateActivity.Request, presenter: CreateActivity.Presenter) {
        val newActivity = request.toDomain()
        when (newActivity) {
             is Valid -> {
                 try {
                     val id = activityGateway.create(newActivity.a)
                     presenter.success(id)

                 } catch (e : Throwable) {
                     presenter.accessDenied()
                 }
             }
            is Invalid -> presenter.validationFailed(newActivity.e.all)
        }
    }
}

fun CreateActivity.Request.toDomain(): ValidatedNel<ValidationError, Activity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            this.title.activityTitle(),
            Valid(this.summary)
    ) {(title, summary) ->
        Activity.new(title, summary, 1)
    }.fix()
}