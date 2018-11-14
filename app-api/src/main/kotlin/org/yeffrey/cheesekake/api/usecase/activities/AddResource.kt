package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.entities.Activity

interface AddResource : UseCase<AddResource.Request, AddResource.Presenter, Activity> {
    data class Request(val user: Option<Int>, val activityId: Int, val resourceId: Int, val quantity: Int) : UseCaseRequest(user)
    interface Presenter : UseCasePresenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun success(id: Int)
    }
}