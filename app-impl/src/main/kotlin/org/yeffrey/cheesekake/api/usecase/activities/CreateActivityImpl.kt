package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Either
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.ActivityCreated
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetails

class CreateActivityImpl(private val activityGateway: CreateActivityGateway) : CreateActivity {
    override suspend fun handle(request: CreateActivity.Request, presenter: CreateActivity.Presenter) = mustBeAuthenticated(request, presenter) { userId ->
        val newActivityId = activityGateway.nextIdentity()
        val newActivity = request.toDomain(newActivityId, userId)
        when (newActivity) {
            is Either.Left -> presenter.validationFailed(newActivity.a)
            is Either.Right -> presenter.success(activityGateway.activityCreated(newActivity.b.event))
        }
    }
}

fun CreateActivity.Request.toDomain(authorId: Int, newId: Int): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityCreated>> = ActivityDetails.new(newId, this.title, this.summary, authorId)
