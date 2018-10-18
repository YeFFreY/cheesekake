package org.yeffrey.cheesekake.api.usecase.activities

import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.activityTitle

class UpdateActivityImpl(private val activityGateway: UpdateActivityGateway) : UpdateActivity {
    override suspend fun handle(request: UpdateActivity.Request, presenter: UpdateActivity.Presenter) {
        activityGateway.get(request.activityId).fold( { presenter.notFound(request.activityId) }) { activity ->
            val updatedActivity = request.toDomain(activity)
            when (updatedActivity) {
                is Valid -> {
                    val id = activityGateway.update(updatedActivity.a)
                    presenter.success(id)
                }
                is Invalid -> presenter.validationFailed(updatedActivity.e.all)
            }
        }
    }
}
fun UpdateActivity.Request.toDomain(activity: Activity): ValidatedNel<ValidationError, Activity> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            this.title.activityTitle(),
            Valid(this.summary)
    ) {(title, summary) ->
        activity.update(title, summary)
    }.fix()
}