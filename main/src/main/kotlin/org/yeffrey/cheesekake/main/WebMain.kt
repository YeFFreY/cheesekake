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
import kotlinx.html.*
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.yeffrey.cheesekake.persistence.DatabaseManager
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.random.Random


data class Skill(val id: Int, val name: String)
data class MinMax(val min: Int, val max: Int)
data class ActivityMetadata(val duration: MinMax?, val participants: MinMax?, val age: MinMax?)
data class Activity(val id: Int, val title: String, val summary: String, val meta: ActivityMetadata?, val skills: List<Skill> = emptyList())
data class GraphqlRequest(var query: String?, var operationName: String?, var variables: Map<String, Any>?)

val activities = mutableListOf(
        Activity(1, "bob", "theBob", ActivityMetadata(MinMax(5, 20), MinMax(3, 7), MinMax(3, 5))),
        Activity(2, "bob 2", "theBob 2", null),
        Activity(3, "bob 3", "theBob 3", ActivityMetadata(MinMax(20, 50), null, MinMax(5, 10)))
)
val skills = listOf(
        listOf(
                Skill(1, "sKill1")
        ),
        listOf(
                Skill(1, "sKill1"),
                Skill(2, "sKill2")
        ),
        null
)

fun TypeRuntimeWiring.Builder.route(build: TypeRuntimeWiring.Builder.() -> Unit): TypeRuntimeWiring.Builder = apply(build)
fun TypeRuntimeWiring.Builder.activityQueries() {
    dataFetcher("activities") { activities }
    dataFetcher("activity") {
        val id: Int = (it.arguments["id"] as String).toInt()
        activities.stream().filter { it.id == id }.findFirst()
    }
}

fun TypeRuntimeWiring.Builder.activityMutations() {
    dataFetcher("createActivity") {
        activities.add(Activity(Random.nextInt(), it.arguments["title"] as String, it.arguments["summary"] as String, null))
        activities.last()
    }
}

fun TypeRuntimeWiring.Builder.activityType() {
    dataFetcher("skills") {
        val dataLoader = it.getDataLoader<Int, List<Skill>>("skill")
        dataLoader.load((it.getSource() as Activity).id)
    }
}

fun TypeRuntimeWiring.Builder.skillQueries(): TypeRuntimeWiring.Builder {
    dataFetcher("skills") {
        skills.get(1)
    }
    return this
}

fun Application.main() {


    val skillsBatchLoader = BatchLoader<Int, List<Skill>> {
        println(it)
        CompletableFuture.supplyAsync {
            val res = mutableListOf<List<Skill>?>()
            (0..(it.size - 1)).forEach {
                res.add(skills.get((0..2).random()))
            }
            res.toList()
        }
    }


    val schemaParser = SchemaParser()
    val typeDefinitionRegistry = schemaParser.parse(File(ClassLoader.getSystemResource("schema.graphqls").file))

    val runtimeWiring = newRuntimeWiring()
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                    .route {
                        activityQueries()
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

                val skillDataLoader = DataLoader.newDataLoader(skillsBatchLoader)
                val registry = DataLoaderRegistry()
                registry.register("skill", skillDataLoader)
                val executionInput = newExecutionInput()
                        .query(graphqlRequest.query)
                        .dataLoaderRegistry(registry)
                        .operationName(graphqlRequest.operationName)
                        .variables(graphqlRequest.variables)

                val executionResult = graphql.execute(executionInput.build())
                call.respond(executionResult.toSpecification())
            }
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}