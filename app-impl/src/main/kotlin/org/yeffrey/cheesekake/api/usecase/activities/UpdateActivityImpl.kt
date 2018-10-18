package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.toOption
import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.UpdateActivitySummaryGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.activityTitle

class UpdateActivityImpl(private val activityGateway: UpdateActivitySummaryGateway) : UpdateActivity {
    override suspend fun update(request: UpdateActivity.Request, presenter: UpdateActivity.Presenter) {
        val updatedActivity = request.toDomain()
        when (updatedActivity) {
            is Valid -> {
                val id = activityGateway.update(updatedActivity.a)
                presenter.success(id)
            }
            is Invalid -> presenter.validationFailed(updatedActivity.e.all)
        }
    }
}

fun UpdateActivity.Request.toDomain(): ValidatedNel<ValidationError, Activity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            this.title.activityTitle(),
            Valid(this.summary)
    ) {(title, summary) ->
        Activity(this.activityId.toOption(), title, summary, 1)
    }.fix()
}