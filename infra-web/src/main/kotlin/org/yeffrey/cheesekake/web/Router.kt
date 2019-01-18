package org.yeffrey.cheesekake.web

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

interface GraphqlHandler {
    operator fun invoke(request: GraphqlRequest)
}

class Router {

    operator fun invoke(): RoutingHttpHandler = routes(
            "/api" bind routes(
                    "/graphql" bind Method.POST to { Response(Status.OK).body("Yo") },
                    "/" bind Method.GET to { Response(Status.OK).body("API root") }
            )
    )

}