package org.yeffrey.cheesekake.main

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import mu.KotlinLogging
import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.yeffrey.cheesekake.web.Router


fun main(args: Array<String>) {

    val config = systemProperties() overriding
            ConfigurationProperties.fromResource("defaults.properties")

    val server = startApplication(config)
    server.block()
}

fun startApplication(config: Configuration): Http4kServer {
    val logger = KotlinLogging.logger("main")

    val serverPort = config[Key("server.port", intType)]

    logger.info { "Starting server..." }

    val app = Router()()
    val server = app.asServer(SunHttp(serverPort))
    server.start()

    logger.info { "Started server on port $serverPort" }

    return server
}
/*
    val schemaParser = SchemaParser()
    val typeDefinitionRegistry = schemaParser.parse(File(ClassLoader.getSystemResource("schema.graphqls").file))
    val skillsByActivityLoader = skillsByActivityLoader()
    val runtimeWiring = newRuntimeWiring()
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                    .route {
                        activityQueries(QueryMyActivitiesImpl())
                        skillQueries()
                    }
            )
            .type(TypeRuntimeWiring.newTypeWiring("Activity")
                    .route {
                        activityType()
                    }
            )
            .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                    .route {
                        activityMutations()
                    }
            )
            .build()

    val schemaGenerator = SchemaGenerator()
    val graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

    val graphql = GraphQL.newGraphQL(graphQLSchema).build()





    DatabaseManager.initialize(this.environment.config.config("database").property("connectionUrl").getString())


                val graphqlRequest = call.receive<GraphqlRequest>()

                val skillDataLoader = DataLoader.newDataLoader(skillsByActivityLoader)
                val registry = DataLoaderRegistry()
                registry.register("skill", skillDataLoader)
                val executionInput = newExecutionInput()
                        .query(graphqlRequest.query)
                        .dataLoaderRegistry(registry)
                        .operationName(graphqlRequest.operationName)
                        .variables(graphqlRequest.variables)
                val executionResult = graphql.execute(executionInput.build())*/


