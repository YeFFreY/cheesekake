package org.yeffrey.cheesekake.web.activities

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.*
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivities
import org.yeffrey.cheesekake.api.usecase.activities.UpdateActivity


fun Route.activities(createActivity: CreateActivity, updateActivity: UpdateActivity, queryActivities: QueryActivities) {
    route("/activities") {
        get {
            val queryTitleContains = call.request.queryParameters["titleContains"]
            queryActivities.handle(QueryActivities.Request(queryTitleContains), QueryActivitiesPresenter(call))
        }
        post {
            val input = call.receive<CreateActivityDto>()
            createActivity.handle(input.toRequest(1), CreateActivityPresenter(call))
        }
        put {
            val input = call.receive<UpdateActivityDto>()
            updateActivity.handle(input.toRequest(1), UpdateActivityPresenter(call))

        }
    }
}