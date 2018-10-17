package org.yeffrey.cheesekake.web.users

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import org.yeffrey.cheesekake.api.usecase.users.RegisterUser

fun Route.users(registerUser: RegisterUser) {
    route("/users") {
        route("/sessions") {

        }
        route("/registration") {
            post {
                val registration = call.receive<RegistrationDto>()
                registerUser.register(registration.toRequest(), RegistrationPresenter(call))
            }
        }
    }
}