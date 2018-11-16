package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivities
import org.yeffrey.cheesekake.web.WebPresenter
import org.yeffrey.cheesekake.web.WebResource

class QueryActivitiesPresenter(override val call: ApplicationCall) : QueryActivities.Presenter, WebPresenter {

    override suspend fun success(activities: List<QueryActivities.Presenter.Activity>) {
        val webResources = activities.map { activity ->
            WebResource(activity, ActivitiesRoutes.hrefs(activity, call))
        }
        call.respond(webResources)
    }
}