package org.yeffrey.cheesekake.api.usecase.users

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest

interface RegisterUser : UseCase<RegisterUser.Request, RegisterUser.Presenter> {
    data class Request(val username: String, val password: String) : UseCaseRequest()
    interface Presenter : UseCasePresenter {
        suspend fun success(id: Int)
    }
}