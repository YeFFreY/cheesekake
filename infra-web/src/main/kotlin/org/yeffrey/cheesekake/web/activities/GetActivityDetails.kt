package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.GetActivityDetails

class GetActivityDetailsPresenter(private val call: ApplicationCall) : GetActivityDetails.Presenter {

    override suspend fun success(activity: GetActivityDetails.Presenter.ActivityDetails) {
        call.respond(activity)
    }

    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun notFound(id: Int) {
        call.respond(HttpStatusCode.NotFound, id)
    }
}