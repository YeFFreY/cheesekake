package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Either
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityCreatedTwo
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetails

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun handle(request: CreateActivity.Request, presenter: CreateActivity.Presenter) = mustBeAuthenticated(request, presenter) {
        val newActivityId = activityGateway.nextIdentity()
        val newActivity = request.toDomain(newActivityId)
        when (newActivity) {
            is Either.Right -> presenter.success(activityGateway.activityCreated(newActivity.b.event))
            is Either.Left -> presenter.validationFailed(newActivity.a)
        }
    }
}

fun CreateActivity.Request.toDomain(newId: Int): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityCreatedTwo>> = ActivityDetails.new(newId, this.title, this.summary)
