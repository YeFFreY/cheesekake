package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.GetActivityDetails
import org.yeffrey.cheesekake.web.WebPresenter
import org.yeffrey.cheesekake.web.WebResource

class GetActivityDetailsPresenter(override val call: ApplicationCall) : GetActivityDetails.Presenter, WebPresenter {
    override suspend fun success(activity: GetActivityDetails.Presenter.ActivityDetails) {
        call.respond(WebResource(activity, ActivitiesRoutes.hrefs(activity, call)))
    }

}