package org.yeffrey.cheesekake.api.usecase.users

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.core.error.ErrorDescription

interface RegisterUser : UseCase<RegisterUser.Request, RegisterUser.Presenter> {
    data class Request(val username: String, val password: String) : UseCaseRequest()
    interface Presenter : UseCasePresenter {
        suspend fun validationFailed(errors: List<ErrorDescription>)
        suspend fun success(id: Int)
    }
}