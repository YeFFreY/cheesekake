package org.yeffrey.cheesekake.main

import arrow.core.Option
import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import mu.KotlinLogging
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.server.ApacheServer
import org.http4k.server.Http4kServer
import org.http4k.server.asServer
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivityImpl
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivityImpl
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivitiesImpl
import org.yeffrey.cheesekake.api.usecase.skills.QueryMySkillsImpl
import org.yeffrey.cheesekake.api.usecase.skills.QuerySkillsByActivitiesImpl
import org.yeffrey.cheesekake.persistence.ActivitiesGatewayImpl
import org.yeffrey.cheesekake.persistence.DatabaseManager
import org.yeffrey.cheesekake.persistence.SkillGatewayImpl
import org.yeffrey.cheesekake.web.Router
import org.yeffrey.cheesekake.web.Session
import org.yeffrey.cheesekake.web.api.GraphqlHandlerImpl
import org.yeffrey.cheesekake.web.core.filter.AuthenticationFilter
import org.yeffrey.cheesekake.web.core.filter.InMemorySessionProvider
import org.yeffrey.cheesekake.web.core.filter.SessionFilter


fun main(args: Array<String>) {

    val config = systemProperties() overriding
            ConfigurationProperties.fromResource("defaults.properties")

    val server = startApplication(config)
    server.block()
}


fun startApplication(config: Configuration): Http4kServer {
    val logger = KotlinLogging.logger("main")

    val serverPort = config[Key("server.port", intType)]
    val dbUrl = config[Key("database.connectionUrl", stringType)]

    logger.info { "Starting server..." }
    val contexts = RequestContexts()

    val sessionKey = RequestContextKey.required<Session>(contexts)

    DatabaseManager.initialize(dbUrl)

    val skillGateway = SkillGatewayImpl()
    val activitiesGateway = ActivitiesGatewayImpl()
    val queryMyActivities = QueryMyActivitiesImpl(activitiesGateway)
    val queryActivity = QueryActivityImpl(activitiesGateway)
    val createActivity = CreateActivityImpl(activitiesGateway, activitiesGateway)
    val queryMySkills = QueryMySkillsImpl(skillGateway)
    val querySkillsByActivities = QuerySkillsByActivitiesImpl(skillGateway)

    val graphqlHandler = GraphqlHandlerImpl(queryMyActivities, queryActivity, createActivity, queryMySkills, querySkillsByActivities)

    val sess = SessionFilter("CK_SESSION", sessionKey, InMemorySessionProvider()) {
        Session(Option.empty())
    }
    val app = ServerFilters.InitialiseRequestContext(contexts)
            .then(sess)
            .then(AuthenticationFilter.Authenticated<Session>()(sessionKey))
            .then(Router(graphqlHandler, sessionKey)())


    val server = app.asServer(ApacheServer(serverPort))
    server.start()

    logger.info { "Started server on port $serverPort" }

    return server
}