package org.yeffrey.cheesekake.main

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.util.error
import kotlinx.html.*
import org.yeffrey.cheesekake.api.usecase.activities.*
import org.yeffrey.cheesekake.api.usecase.users.RegisterUserImpl
import org.yeffrey.cheesekake.persistence.DatabaseManager
import org.yeffrey.cheesekake.persistence.activities.ActivityGatewayImpl
import org.yeffrey.cheesekake.persistence.users.UserGatewayImpl
import org.yeffrey.cheesekake.web.CheeseError
import org.yeffrey.cheesekake.web.CheeseKakeSesion
import org.yeffrey.cheesekake.web.CheesePrincipal
import org.yeffrey.cheesekake.web.activities.activities
import org.yeffrey.cheesekake.web.index
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
    val removeResource = RemoveResourceImpl(activityGateway)
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
                this.sessions.get<CheeseKakeSesion>()?.userId?.let { userId -> CheesePrincipal(userId) }
            }
        }
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            environment.log.error(cause)
            //TODO throw HTTP 400 when invalid request not 500 : DataConversionException is not thrown by ktor for primitive type conversion...
            call.respond(HttpStatusCode.InternalServerError, CheeseError("Unexpected error occurred"))
        }
    }
    install(Locations)
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        anyHost()
        allowCredentials = true
        method(HttpMethod.Options)
        method(HttpMethod.Put)

    }
    install(Sessions) {
        cookie<CheeseKakeSesion>("CHEESEKAKE_SESSION_ID", directorySessionStorage(File(".sessions"), cached = true)) {
            cookie.path = "/"
        }
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
                index()
                activities(createActivity, updateActivity, queryActivities, getActivityDetails, addResource, removeResource)
            }
        }
        users(registerUser)

    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}