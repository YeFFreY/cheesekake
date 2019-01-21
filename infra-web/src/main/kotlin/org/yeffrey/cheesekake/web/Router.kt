package org.yeffrey.cheesekake.web

import arrow.core.Option
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

interface GraphqlHandler {
    operator fun invoke(request: GraphqlRequest): MutableMap<String, Any>
}

data class Session(val principal: Option<Int>)

class Router(val graphqlHandler: GraphqlHandler, val key: RequestContextLens<Session>) {

    private val graphqlRequestLens = Body.auto<GraphqlRequest>().toLens()
    private val graphqlResponseLens = Body.auto<MutableMap<String, Any>>().toLens()

    operator fun invoke(): RoutingHttpHandler = routes(
            "/api" bind routes(
                    "/graphql" bind Method.POST to processGraphql(),
                    "/" bind Method.GET to { Response(Status.OK).body("API root") }
            )
    )

    private fun processGraphql() = { req: Request ->
        val request = req.with(key of (key(req).copy(principal = Option.just(109))))
        val newGraphqlRequest = graphqlRequestLens(request)
        val result = graphqlHandler(newGraphqlRequest)
        Response(Status.OK).with(graphqlResponseLens of result)
    }

}