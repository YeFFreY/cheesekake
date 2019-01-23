package org.yeffrey.cheesekake.api.usecase.users

import org.yeffrey.cheesekake.api.usecase.UseCase

interface LoginUser : UseCase<LoginUser.Request, Unit> {
    data class Request(val email: String, val password: String)
}