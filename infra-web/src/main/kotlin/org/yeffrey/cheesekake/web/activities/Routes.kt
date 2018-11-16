package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.routing.Route
import org.yeffrey.cheesekake.api.usecase.Resource
import org.yeffrey.cheesekake.api.usecase.activities.*
import org.yeffrey.cheesekake.web.WebAction
import org.yeffrey.cheesekake.web.withPrincipalId

@Location("/activities")
class ActivitiesSummary

@Location("/activities/{activityId}")
data class ActivityDetails(val activityId: Int)

@Location("/activities/creation")
class ActivityCreation

@Location("/activities/{activityId}/correction")
data class ActivityCorrection(val activityId: Int)

@Location("/activities/{activityId}/resourceAddition")
data class ResourceAddition(val activityId: Int)

class ActivitiesRoutes {
    companion object {
        fun hrefs(resource: Resource, call: ApplicationCall): List<WebAction> {
            var links: MutableList<WebAction> = mutableListOf()
            resource.actions.map { action ->

                links.add(WebAction("activity:self", call.application.locations.href(ActivityDetails(resource.id))))
                if (action.name == "editable") {
                    links.add(WebAction("activity:details:correction", call.application.locations.href(ActivityCorrection(resource.id))))
                    links.add(WebAction("activity:resources:addition", call.application.locations.href(ResourceAddition(resource.id))))
                }
            }
            return links.toList()
        }
    }

}


fun Route.activities(createActivity: CreateActivity, updateActivity: UpdateActivity, queryActivities: QueryActivities, getActivityDetails: GetActivityDetails, addResource: AddResource) {
    get<ActivitiesSummary> {
        val queryTitleContains = call.request.queryParameters["titleContains"]
        queryActivities.handle(QueryActivities.Request(queryTitleContains), QueryActivitiesPresenter(call), withPrincipalId(call))
    }
    get<ActivityDetails> { details ->
        getActivityDetails.handle(GetActivityDetails.Request(details.activityId), GetActivityDetailsPresenter(call), withPrincipalId(call))
    }

    post<ActivityCreation> {
        val input = call.receive<CreateActivityDto>()
        createActivity.handle(input.toRequest(), CreateActivityPresenter(call), withPrincipalId(call))
    }
    post<ActivityCorrection> { detailsCorrection ->
        val input = call.receive<UpdateActivityDto>()
        updateActivity.handle(input.toRequest(detailsCorrection.activityId), UpdateActivityPresenter(call), withPrincipalId(call))
    }

    post<ResourceAddition> { resourceAddition ->
        val input = call.receive<AddResourceDto>()
        addResource.handle(input.toRequest(resourceAddition.activityId), AddResourcePresenter(call), withPrincipalId(call))
    }
}