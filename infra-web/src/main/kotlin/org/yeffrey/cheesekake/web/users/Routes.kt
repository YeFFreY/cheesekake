package org.yeffrey.cheesekake.web.users

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.html.respondHtml
import io.ktor.request.receive
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.html.*
import org.yeffrey.cheesekake.api.usecase.users.RegisterUser
import org.yeffrey.cheesekake.web.CheeseKakeSesion
import org.yeffrey.cheesekake.web.CheesePrincipal

fun Route.users(registerUser: RegisterUser) {
    route("/users") {

        route("/login") {
            get {
                call.respondHtml {
                    head {
                        title { +"Cheesekake" }
                    }
                    body {
                        h1 {
                            +"Login"
                        }
                        form(encType = FormEncType.applicationXWwwFormUrlEncoded, method = FormMethod.post) {
                            label {
                                +"Username"
                                textInput {
                                    name = "user"
                                }
                            }
                            label {
                                +"Password"
                                passwordInput {
                                    name = "password"
                                }
                            }
                            submitInput() {
                                value = "Login"
                            }
                        }
                    }
                }
            }
            authenticate("login") {
                post {
                    val principal = call.principal<CheesePrincipal>()
                    call.sessions.set(CheeseKakeSesion(principal!!.userId))
                    call.respondRedirect("/", false)
                }
            }
        }

        route("/registration") {
            post {
                val registration = call.receive<RegistrationDto>()
                registerUser.handle(registration.toRequest(), RegistrationPresenter(call))
            }
        }
    }
}