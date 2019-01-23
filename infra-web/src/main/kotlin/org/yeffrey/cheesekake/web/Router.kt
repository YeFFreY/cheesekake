package org.yeffrey.cheesekake.web

import arrow.core.Option
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.yeffrey.cheesekake.web.api.users.UsersHandlerImpl
import org.yeffrey.cheesekake.web.core.filter.Session
import org.yeffrey.cheesekake.web.core.filter.Sessions

interface GraphqlHandler {
    operator fun invoke(request: GraphqlRequest): MutableMap<String, Any>
}


class Router(val graphqlHandler: GraphqlHandler, val userHandler: UsersHandlerImpl, val key: RequestContextLens<Session>) {

    private val graphqlRequestLens = Body.auto<GraphqlRequest>().toLens()
    private val graphqlResponseLens = Body.auto<MutableMap<String, Any>>().toLens()

    private val authenticatedRoutes = Sessions.Authenticated(key).then(
            routes(
                    "/graphql" bind Method.POST to processGraphql()
            )
    )

    operator fun invoke(): RoutingHttpHandler = routes(
            "/api" bind routes(
                    "/" bind Method.GET to { Response(Status.OK).body("API root") },
                    authenticatedRoutes,
                    userHandler()
            )
    )

    private fun processGraphql() = { req: Request ->
        val request = req.with(key of (key(req).copy(principal = Option.just(109))))

        val newGraphqlRequest = graphqlRequestLens(request)
        val result = graphqlHandler(newGraphqlRequest)
        Response(Status.OK).with(graphqlResponseLens of result)
    }

}