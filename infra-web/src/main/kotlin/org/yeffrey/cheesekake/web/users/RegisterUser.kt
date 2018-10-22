package org.yeffrey.cheesekake.web.users

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.yeffrey.cheesekake.api.usecase.users.RegisterUser
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.web.CheeseKakeSesion

data class RegistrationDto(val username: String, val password: String)

fun RegistrationDto.toRequest(): RegisterUser.Request {
    return RegisterUser.Request(this.username, this.password)
}

class RegistrationPresenter(private val call: ApplicationCall) : RegisterUser.Presenter {
    override suspend fun notFound(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun validationFailed(errors: List<ValidationError>) {
        call.respond(HttpStatusCode.BadRequest, errors)
    }

    override suspend fun success(id: Int) {
        call.sessions.set(CheeseKakeSesion(id))
        call.respond(id)
    }
}