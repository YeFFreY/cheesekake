package org.yeffrey.cheesekake.web

import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.yeffrey.cheesekake.web.api.users.UsersHandlerImpl
import org.yeffrey.cheesekake.web.core.filter.Session

interface GraphqlHandler {
    operator fun invoke(request: GraphqlRequest, session: Session): MutableMap<String, Any>
}


class Router(val graphqlHandler: GraphqlHandler, val userHandler: UsersHandlerImpl, val key: RequestContextLens<Session>) {

    private val graphqlRequestLens = Body.auto<GraphqlRequest>().toLens()
    private val graphqlResponseLens = Body.auto<MutableMap<String, Any>>().toLens()

    private val authenticatedRoutes = routes(
            "/graphql" bind Method.POST to processGraphql()
    )

    operator fun invoke(): RoutingHttpHandler = routes(
            "/api" bind routes(
                    "/" bind Method.GET to { Response(Status.OK).body("API root") },
                    authenticatedRoutes,
                    userHandler()
            )
    )

    private fun processGraphql() = { request: Request ->
        val session = key(request)

        val newGraphqlRequest = graphqlRequestLens(request)
        val result = graphqlHandler(newGraphqlRequest, session)
        Response(Status.OK).with(graphqlResponseLens of result)
    }

}