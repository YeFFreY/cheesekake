package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Either
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetails
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetailsCorrected
import org.yeffrey.cheesekake.domain.activities.isAuthor
import org.yeffrey.cheesekake.domain.respect

class UpdateActivityImpl(private val activityGateway: UpdateActivityGateway) : UpdateActivity {
    override suspend fun handle(request: UpdateActivity.Request, presenter: UpdateActivity.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        activityGateway.getDescription(request.activityId).fold({ presenter.notFound(request.activityId) }) { activity ->
            when (respect(userId, activity, ::isAuthor)) {
                true -> process(request.toDomain(activity), presenter)
                false -> presenter.accessDenied()
            }
        }
    }

    private suspend fun process(result: Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityDetailsCorrected>>, presenter: UpdateActivity.Presenter) {
        when (result) {
            is Either.Left -> presenter.validationFailed(result.a)
            is Either.Right -> presenter.success(activityGateway.descriptionUpdated(result.b.event))
        }
    }


}

fun UpdateActivity.Request.toDomain(activity: ActivityDetails): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityDetailsCorrected>> = activity.updateActivityDetails(this.title, this.summary)