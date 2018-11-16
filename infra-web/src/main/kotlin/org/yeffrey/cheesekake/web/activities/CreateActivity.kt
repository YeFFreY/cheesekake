package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.core.error.ErrorDescription

data class CreateActivityDto(val title: String = "", val summary: String = "")

fun CreateActivityDto.toRequest(): CreateActivity.Request = CreateActivity.Request(this.title, this.summary)

class CreateActivityPresenter(private val call: ApplicationCall) : CreateActivity.Presenter {
    override suspend fun notFound(id: Int) {
        call.respond(HttpStatusCode.NotFound, id)
    }

    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun validationFailed(errors: List<ErrorDescription>) {
        call.respond(HttpStatusCode.BadRequest, errors)
    }

    override suspend fun success(id: Int) {
        call.respond(id)
    }

}