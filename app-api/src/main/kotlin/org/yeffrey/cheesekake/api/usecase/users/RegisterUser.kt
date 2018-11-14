package org.yeffrey.cheesekake.api.usecase.users

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.users.entities.User

interface RegisterUser : UseCase<RegisterUser.Request, RegisterUser.Presenter, User> {
    data class Request(val username: String, val password: String) : UseCaseRequest()
    interface Presenter : UseCasePresenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun success(id: Int)
    }
}