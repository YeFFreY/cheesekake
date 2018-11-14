package org.yeffrey.cheesekake.web.activities

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.*
import org.yeffrey.cheesekake.api.usecase.activities.*
import org.yeffrey.cheesekake.web.withPrincipalId


fun Route.activities(createActivity: CreateActivity, updateActivity: UpdateActivity, queryActivities: QueryActivities, getActivityDetails: GetActivityDetails, addResource: AddResource) {
    route("/activities") {
        get {
            val queryTitleContains = call.request.queryParameters["titleContains"]
            queryActivities.handle(QueryActivities.Request(queryTitleContains), QueryActivitiesPresenter(call))
        }
        post {
            val input = call.receive<CreateActivityDto>()
            createActivity.handle(input.toRequest(5), CreateActivityPresenter(call))
        }
        route("{activityId}") {
            get {
                val activityId = call.parameters["activityId"]?.toInt() ?: -1
                getActivityDetails.handle(GetActivityDetails.Request(activityId), GetActivityDetailsPresenter(call))
            }
            put {
                val input = call.receive<UpdateActivityDto>()
                val activityId = call.parameters["activityId"]?.toInt() ?: -1
                updateActivity.handle(input.toRequest(withPrincipalId(call), activityId), UpdateActivityPresenter(call))
            }

            route("resources") {
                post {
                    val input = call.receive<AddResourceDto>()
                    val activityId = call.parameters["activityId"]?.toInt() ?: -1
                    addResource.handle(input.toRequest(activityId, withPrincipalId(call)), AddResourcePresenter(call))
                }
            }
        }
    }
}