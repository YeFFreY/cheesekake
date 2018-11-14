package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetails
import org.yeffrey.cheesekake.domain.activities.entities.ActivityPolicy

abstract class UpdateActivity : UseCase<UpdateActivity.Request, UpdateActivity.Presenter, ActivityDetails>(listOf(ActivityPolicy.IsAuthor())) {
    data class Request(val user: Option<Int>, val activityId: Int, val title: String, val summary: String) : UseCaseRequest(user)
    interface Presenter : UseCasePresenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun success(id: Int)
    }
}