package org.yeffrey.cheesekake.main

import com.fasterxml.jackson.databind.DeserializationFeature
import graphql.ExecutionInput.newExecutionInput
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.future.await
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.html.*
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivitiesImpl
import org.yeffrey.cheesekake.persistence.DatabaseManager
import org.yeffrey.cheesekake.web.GraphqlRequest
import org.yeffrey.cheesekake.web.api.activities.activityMutations
import org.yeffrey.cheesekake.web.api.activities.activityQueries
import org.yeffrey.cheesekake.web.api.activities.activityType
import org.yeffrey.cheesekake.web.api.activities.skillsByActivityLoader
import org.yeffrey.cheesekake.web.api.skills.skillQueries
import org.yeffrey.cheesekake.web.route
import java.io.File


fun Application.main() {


    val compute = newFixedThreadPoolContext(4, "compute")

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

    install(Locations)
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        anyHost()
        allowCredentials = true
        method(HttpMethod.Options)
        method(HttpMethod.Put)

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
        route("/graphql") {
            post {

                val graphqlRequest = call.receive<GraphqlRequest>()

                val skillDataLoader = DataLoader.newDataLoader(skillsByActivityLoader)
                val registry = DataLoaderRegistry()
                registry.register("skill", skillDataLoader)
                val executionInput = newExecutionInput()
                        .query(graphqlRequest.query)
                        .dataLoaderRegistry(registry)
                        .operationName(graphqlRequest.operationName)
                        .variables(graphqlRequest.variables)
                val executionResult = graphql.executeAsync(executionInput.build()).await()
                call.respond(executionResult.toSpecification())
            }
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}