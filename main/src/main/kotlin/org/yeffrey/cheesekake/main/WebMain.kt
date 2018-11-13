package org.yeffrey.cheesekake.main

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import kotlinx.html.*
import org.yeffrey.cheesekake.api.usecase.activities.*
import org.yeffrey.cheesekake.api.usecase.users.RegisterUserImpl
import org.yeffrey.cheesekake.persistence.DatabaseManager
import org.yeffrey.cheesekake.persistence.activities.ActivityGatewayImpl
import org.yeffrey.cheesekake.persistence.users.UserGatewayImpl
import org.yeffrey.cheesekake.web.CheeseKakeSesion
import org.yeffrey.cheesekake.web.CheesePrincipal
import org.yeffrey.cheesekake.web.activities.activities
import org.yeffrey.cheesekake.web.users.users
import java.io.File


fun Application.main() {

    DatabaseManager.initialize(this.environment.config.config("database").property("connectionUrl").getString())
    val activityGateway = ActivityGatewayImpl()
    val userGateway = UserGatewayImpl()
    val createActivity = CreateActivityImpl(activityGateway)
    val updateActivity = UpdateActivityImpl(activityGateway)
    val queryActivities = QueryActivitiesImpl(activityGateway)
    val getActivityDetails = GetActivityDetailsImpl(activityGateway)
    val addResource = AddResourceImpl(activityGateway)
    val registerUser = RegisterUserImpl(userGateway)

    install(Authentication) {
        form("login") {
            validate { credentials ->
                userGateway.login(credentials.name, credentials.password).fold({ null }) {
                    CheesePrincipal(it)
                }
            }
        }
        session<CheeseKakeSesion>("authenticated") {
            challenge = SessionAuthChallenge.Unauthorized
            validate {
                val session = this.sessions.get<CheeseKakeSesion>()
                session?.userId?.let { userId -> CheesePrincipal(userId) }
            }
        }
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        allowCredentials = true
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        host("*")
    }
    install(Sessions) {
        cookie<CheeseKakeSesion>("CHEESEKAKE_SESSION_ID", directorySessionStorage(File(".sessions"), cached = true))
    }
    install(ContentNegotiation) {
        jackson {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }
    install(Routing) {
        route("/") {
            get {
                call.respondHtml {
                    head {
                        title { +"Cheesekake" }
                    }
                    body {
                        h1 {
                            +"Welcome"
                        }
                        p {
                            +"Welcome on Cheesekake"
                        }
                        a("/users/login") {
                            +"Login"
                        }
                    }
                }
            }
        }
        authenticate("authenticated") {
            route("/api") {
                activities(createActivity, updateActivity, queryActivities, getActivityDetails, addResource)
            }
        }
        users(registerUser)

    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}