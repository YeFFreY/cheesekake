package org.yeffrey.cheesekake.api.usecase.users

import org.yeffrey.cheesekake.domain.ValidationError

interface RegisterUser {
    suspend fun register(request: Request, presenter: Presenter)
    data class Request(val username: String, val password: String)
    interface Presenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun success(id: Int)
    }
}