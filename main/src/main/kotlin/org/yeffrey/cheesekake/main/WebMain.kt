package org.yeffrey.cheesekake.main

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import mu.KotlinLogging
import org.http4k.server.ApacheServer
import org.http4k.server.Http4kServer
import org.http4k.server.asServer
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivityImpl
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivitiesImpl
import org.yeffrey.cheesekake.api.usecase.skills.QueryMySkillsImpl
import org.yeffrey.cheesekake.api.usecase.skills.QuerySkillsByActivitiesImpl
import org.yeffrey.cheesekake.persistence.ActivitiesGatewayImpl
import org.yeffrey.cheesekake.persistence.DatabaseManager
import org.yeffrey.cheesekake.persistence.SkillGatewayImpl
import org.yeffrey.cheesekake.web.Router
import org.yeffrey.cheesekake.web.api.GraphqlHandlerImpl


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
    DatabaseManager.initialize(dbUrl)

    val skillGateway = SkillGatewayImpl()
    val queryMyActivities = QueryMyActivitiesImpl(ActivitiesGatewayImpl())
    val queryActivity = QueryActivityImpl(ActivitiesGatewayImpl())
    val queryMySkills = QueryMySkillsImpl(skillGateway)
    val querySkillsByActivities = QuerySkillsByActivitiesImpl(skillGateway)

    val graphqlHandler = GraphqlHandlerImpl(queryMyActivities, queryActivity, queryMySkills, querySkillsByActivities)

    val app = Router(graphqlHandler)()
    val server = app.asServer(ApacheServer(serverPort))
    server.start()

    logger.info { "Started server on port $serverPort" }

    return server
}