package org.yeffrey.cheesekake.web

import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.response.respond
import io.ktor.routing.Route
import org.yeffrey.cheesekake.web.activities.Activities

@Location("/")
class ApiIndex

data class Api(val version: Int, val links: List<WebAction>)

fun Route.index() {
    get<ApiIndex> {
        call.respond(Api(1, listOf(
                WebAction("activity:creation", call.application.locations.href(Activities.ActivityCreation())),
                WebAction("activity:list", call.application.locations.href(Activities.ActivitiesSummary()))
        )))
    }
}