package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.UpdateActivitySummaryGateway
import org.yeffrey.cheesekake.domain.activities.command.UpdatedActivity
import org.yeffrey.cheesekake.domain.activities.command.activityTitle

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

fun UpdateActivity.Request.toDomain(): ValidatedNel<ValidationError, UpdatedActivity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            this.title.activityTitle(),
            Valid(this.summary)
    ) {(title, summary) ->
        UpdatedActivity(this.activityId, title, summary)
    }.fix()
}