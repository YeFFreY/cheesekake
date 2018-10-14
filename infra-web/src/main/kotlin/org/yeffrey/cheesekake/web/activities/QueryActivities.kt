package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivities

class QueryActivitiesPresenter(private val call: ApplicationCall) : QueryActivities.Presenter {
    override suspend fun success(activities: List<QueryActivities.Presenter.Activity>) {
        call.respond(activities)
    }
}