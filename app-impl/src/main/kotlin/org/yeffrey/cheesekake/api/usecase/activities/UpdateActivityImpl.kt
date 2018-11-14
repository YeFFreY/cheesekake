package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Either
import arrow.core.Option
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetails
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetailsCorrected

class UpdateActivityImpl(private val activityGateway: UpdateActivityGateway) : UpdateActivity() {
    override suspend fun perform(activity: ActivityDetails, request: Request, presenter: Presenter) {
        process(request.toDomain(activity), presenter)
    }

    override suspend fun retrieveAggregate(request: UpdateActivity.Request, presenter: UpdateActivity.Presenter): Option<ActivityDetails> {
        return activityGateway.getDescription(request.activityId)
    }

    private suspend fun process(result: Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityDetailsCorrected>>, presenter: UpdateActivity.Presenter) {
        when (result) {
            is Either.Left -> presenter.validationFailed(result.a)
            is Either.Right -> presenter.success(activityGateway.descriptionUpdated(result.b.event))
        }
    }


}

fun UpdateActivity.Request.toDomain(activity: ActivityDetails): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityDetailsCorrected>> = activity.updateActivityDetails(this.title, this.summary)