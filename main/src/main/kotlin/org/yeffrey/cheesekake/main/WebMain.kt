package org.yeffrey.cheesekake.main

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import mu.KotlinLogging
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.CorsPolicy
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.server.ApacheServer
import org.http4k.server.Http4kServer
import org.http4k.server.asServer
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivityImpl
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivityImpl
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivitiesImpl
import org.yeffrey.cheesekake.api.usecase.activities.UpdateActivityGeneralInformationImpl
import org.yeffrey.cheesekake.api.usecase.activities.categories.QueryActivityCategoriesImpl
import org.yeffrey.cheesekake.api.usecase.skills.CreateSkillImpl
import org.yeffrey.cheesekake.api.usecase.skills.QueryMySkillsImpl
import org.yeffrey.cheesekake.api.usecase.skills.QuerySkillsByActivitiesImpl
import org.yeffrey.cheesekake.api.usecase.users.LoginUserImpl
import org.yeffrey.cheesekake.persistence.*
import org.yeffrey.cheesekake.web.Router
import org.yeffrey.cheesekake.web.api.GraphqlHandlerImpl
import org.yeffrey.cheesekake.web.api.users.UsersHandlerImpl
import org.yeffrey.cheesekake.web.core.filter.InMemorySessionProvider
import org.yeffrey.cheesekake.web.core.filter.Session
import org.yeffrey.cheesekake.web.core.filter.Sessions


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

    val skillGateway = SkillsGatewayImpl()
    val activitiesGateway = ActivitiesGatewayImpl()
    val activityCategoriesGateway = ActivityCategoriesGatewayImpl()
    val userGateway = UserGatewayImpl()
    val loginUser = LoginUserImpl(userGateway)
    val queryActivityCategories = QueryActivityCategoriesImpl(activityCategoriesGateway)
    val queryMyActivities = QueryMyActivitiesImpl(activitiesGateway)
    val queryActivity = QueryActivityImpl(activitiesGateway)
    val createActivity = CreateActivityImpl(activitiesGateway, activitiesGateway)
    val updateActivityGeneralInformation = UpdateActivityGeneralInformationImpl(activitiesGateway, activitiesGateway)
    val createSkill = CreateSkillImpl(skillGateway, skillGateway)
    val queryMySkills = QueryMySkillsImpl(skillGateway)
    val querySkillsByActivities = QuerySkillsByActivitiesImpl(skillGateway)

    val graphqlHandler = GraphqlHandlerImpl(queryActivityCategories, queryMyActivities, queryActivity, createActivity, updateActivityGeneralInformation, createSkill, queryMySkills, querySkillsByActivities)
    val userHandler = UsersHandlerImpl(loginUser)

    val app = ServerFilters.InitialiseRequestContext(contexts)
            .then(ServerFilters.Cors(CorsPolicy.UnsafeGlobalPermissive))
            .then(Sessions.UseSessions("CK_SESSION", sessionKey, InMemorySessionProvider()) { Session() })
            .then(Sessions.FakePrincipal(sessionKey, 1))
            .then(Router(graphqlHandler, userHandler, sessionKey)())


    val server = app.asServer(ApacheServer(serverPort))
    server.start()

    logger.info { "Started server on port $serverPort" }

    return server
}