package org.yeffrey.cheesekake.web.activities

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.*
import org.yeffrey.cheesekake.api.usecase.activities.AddResource
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivities
import org.yeffrey.cheesekake.api.usecase.activities.UpdateActivity


fun Route.activities(createActivity: CreateActivity, updateActivity: UpdateActivity, queryActivities: QueryActivities, addResource: AddResource) {
    route("/activities") {
        get {
            val queryTitleContains = call.request.queryParameters["titleContains"]
            queryActivities.handle(QueryActivities.Request(queryTitleContains), QueryActivitiesPresenter(call))
        }
        post {
            val input = call.receive<CreateActivityDto>()
            createActivity.handle(input.toRequest(5), CreateActivityPresenter(call))
        }
        put {
            val input = call.receive<UpdateActivityDto>()
            updateActivity.handle(input.toRequest(5), UpdateActivityPresenter(call))

        }
        route("{activityId}/resources") {
            post {
                val input = call.receive<AddResourceDto>()
                val activityId = call.parameters["activityId"]?.toInt() ?: -1
                addResource.handle(input.toRequest(activityId, 5), AddResourcePresenter(call))
            }
        }
    }
}