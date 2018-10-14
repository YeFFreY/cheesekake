package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.domain.ValidationError

data class CreateActivityDto(val title: String, val summary: String)

fun CreateActivityDto.toRequest(): CreateActivity.Request {
    return CreateActivity.Request(this.title, this.summary)
}

class CreateActivityPresenter(private val call: ApplicationCall) : CreateActivity.Presenter {
    override suspend fun validationFailed(errors: List<ValidationError>) {
        call.respond(HttpStatusCode.BadRequest,  errors)
    }

    override suspend fun success(id: Int) {
        call.respond(id)
    }

}