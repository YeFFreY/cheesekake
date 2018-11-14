package org.yeffrey.cheesekake.web.activities

import arrow.core.Option
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.UpdateActivity
import org.yeffrey.cheesekake.domain.ValidationError


data class UpdateActivityDto(val title: String = "", val summary: String = "")

fun UpdateActivityDto.toRequest(userId: Option<Int>, activityId: Int): UpdateActivity.Request = UpdateActivity.Request(userId, activityId, this.title, this.summary)

class UpdateActivityPresenter(private val call: ApplicationCall) : UpdateActivity.Presenter {
    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun notFound(id: Int) {
        call.respond(HttpStatusCode.NotFound, id)
    }

    override suspend fun validationFailed(errors: List<ValidationError>) {
        call.respond(HttpStatusCode.BadRequest,  errors)
    }

    override suspend fun success(id: Int) {
        call.respond(id)
    }

}