package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.toOption
import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.ValidationError

interface AddResource : UseCase<AddResource.Request, AddResource.Presenter> {
    data class Request(val user: Int, val activityId: Int, val resourceId: Int) : UseCaseRequest(user.toOption())
    interface Presenter : UseCasePresenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun success(id: Int)
    }
}