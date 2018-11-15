package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.core.error.ErrorDescription

interface AddResource : UseCase<AddResource.Request, AddResource.Presenter> {
    data class Request(val user: Option<Int>, val activityId: Int, val resourceId: Int, val quantity: Int) : UseCaseRequest(user)
    interface Presenter : UseCasePresenter {
        suspend fun validationFailed(errors: List<ErrorDescription>)
        suspend fun success(id: Int)
    }
}