package org.yeffrey.cheesekake.web.activities

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivities


fun Route.activities(createActivity: CreateActivity, queryActivities: QueryActivities) {
    route("/activities") {
        get {
            val queryTitleContains = call.request.queryParameters["titleContains"]
            queryActivities.handle(QueryActivities.Request(queryTitleContains), QueryActivitiesPresenter(call))
        }
        post {
            val input = call.receive<CreateActivityDto>()
            createActivity.handle(input.toRequest(1), CreateActivityPresenter(call))
        }
    }
}