package org.yeffrey.cheesekake.web.users

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.yeffrey.cheesekake.api.usecase.users.RegisterUser
import org.yeffrey.cheesekake.web.CheeseKakeSesion
import org.yeffrey.cheesekake.web.WebPresenter

data class RegistrationDto(val username: String, val password: String)

fun RegistrationDto.toRequest(): RegisterUser.Request {
    return RegisterUser.Request(this.username, this.password)
}

class RegistrationPresenter(override val call: ApplicationCall) : RegisterUser.Presenter, WebPresenter {
    override suspend fun success(id: Int) {
        call.sessions.set(CheeseKakeSesion(id))
        call.respond(id)
    }
}