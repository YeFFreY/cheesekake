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

class Activities {
    companion object {
        const val DETAILS = "/activities/{activityId}"
    }


    @Location("/activities")
    class ActivitiesSummary

    @Location(DETAILS)
    data class ActivityDetails(val activityId: Int)

    @Location("/activities/creation")
    class ActivityCreation

    @Location("/activities/{activityId}/correction")
    data class ActivityCorrection(val activityId: Int)

    @Location("/activities/{activityId}/resourceAddition")
    data class ResourceAddition(val activityId: Int)

    @Location("/activities/{activityId}/resourceRemoval")
    data class ResourceRemoval(val activityId: Int)
}
class ActivitiesRoutes {
    companion object {
        fun hrefs(resource: Resource, call: ApplicationCall): List<WebAction> {
            val links: MutableList<WebAction> = mutableListOf()
            links.add(WebAction("activity:details", call.application.locations.href(Activities.ActivityDetails(resource.id))))
            resource.actions.map { action ->
                if (action.name == "editable") {
                    links.add(WebAction("activity:details:correction", call.application.locations.href(Activities.ActivityCorrection(resource.id))))
                    links.add(WebAction("activity:resources:addition", call.application.locations.href(Activities.ResourceAddition(resource.id))))
                    links.add(WebAction("activity:resources:removal", call.application.locations.href(Activities.ResourceRemoval(resource.id))))
                }
            }
            return links.toList()
        }
    }

}


fun Route.activities(createActivity: CreateActivity, updateActivity: UpdateActivity, queryActivities: QueryActivities, getActivityDetails: GetActivityDetails, addResource: AddResource, removeResource: RemoveResource) {
    get<Activities.ActivitiesSummary> {
        val queryTitleContains = call.request.queryParameters["titleContains"]
        queryActivities.handle(QueryActivities.Request(queryTitleContains), QueryActivitiesPresenter(call), withPrincipalId(call))
    }
    get<Activities.ActivityDetails> { details ->
        getActivityDetails.handle(GetActivityDetails.Request(details.activityId), GetActivityDetailsPresenter(call), withPrincipalId(call))
    }

    post<Activities.ActivityCreation> {
        val input = call.receive<CreateActivityDto>()
        createActivity.handle(input.toRequest(), CreateActivityPresenter(call), withPrincipalId(call))
    }
    post<Activities.ActivityCorrection> { detailsCorrection ->
        val input = call.receive<UpdateActivityDto>()
        updateActivity.handle(input.toRequest(detailsCorrection.activityId), UpdateActivityPresenter(call), withPrincipalId(call))
    }

    post<Activities.ResourceAddition> { resourceAddition ->
        val input = call.receive<AddResourceDto>()
        addResource.handle(input.toRequest(resourceAddition.activityId), AddResourcePresenter(call), withPrincipalId(call))
    }
    post<Activities.ResourceRemoval> { resourceRemoval ->
        val input = call.receive<RemoveResourceDto>()
        removeResource.handle(input.toRequest(resourceRemoval.activityId), RemoveResourcePresenter(call), withPrincipalId(call))
    }
}