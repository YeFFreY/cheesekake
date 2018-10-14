package org.yeffrey.cheesekake.main

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.routing.route
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.directorySessionStorage
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivityImpl
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivitiesImpl
import org.yeffrey.cheesekake.api.usecase.users.RegisterUserImpl
import org.yeffrey.cheesekake.persistence.DatabaseManager
import org.yeffrey.cheesekake.persistence.activities.ActivityGatewayImpl
import org.yeffrey.cheesekake.persistence.users.UserGatewayImpl
import org.yeffrey.cheesekake.web.CheeseKakeSesion
import org.yeffrey.cheesekake.web.activities.activities
import org.yeffrey.cheesekake.web.users.users
import java.io.File


fun Application.main() {

    DatabaseManager.initialize(this.environment.config.config("database").property("connectionUrl").getString())
    val activityGateway = ActivityGatewayImpl()
    val userGateway = UserGatewayImpl()
    val createActivity = CreateActivityImpl(activityGateway)
    val queryActivities = QueryActivitiesImpl(activityGateway)
    val registerUser = RegisterUserImpl(userGateway)

    install(DefaultHeaders)
    install(CallLogging)
    install(Sessions) {
        cookie<CheeseKakeSesion>("CHEESEKAKE_SESSION_ID", directorySessionStorage(File(".sessions"), cached = true))
    }
    install(ContentNegotiation) {
        jackson { }
    }
    install(Routing) {
        route("/api") {
            activities(createActivity, queryActivities)
            users(registerUser)
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}