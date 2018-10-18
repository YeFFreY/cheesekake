package org.yeffrey.cheesekake.web.activities

import arrow.core.toOption
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.domain.ValidationError

data class CreateActivityDto(val title: String, val summary: String)

fun CreateActivityDto.toRequest(userId: Int): CreateActivity.Request {
    val request = CreateActivity.Request(this.title, this.summary)
    request.userId = userId.toOption()
    return request
}

class CreateActivityPresenter(private val call: ApplicationCall) : CreateActivity.Presenter {
    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun validationFailed(errors: List<ValidationError>) {
        call.respond(HttpStatusCode.BadRequest,  errors)
    }

    override suspend fun success(id: Int) {
        call.respond(id)
    }

}