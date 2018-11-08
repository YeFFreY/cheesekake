package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.AddResource
import org.yeffrey.cheesekake.domain.ValidationError

data class AddResourceDto(val resourceId: Int, val quantity: Int)

fun AddResourceDto.toRequest(activityId: Int, userId: Int): AddResource.Request = AddResource.Request(userId, activityId, resourceId, quantity)

class AddResourcePresenter(private val call: ApplicationCall) : AddResource.Presenter {
    override suspend fun validationFailed(errors: List<ValidationError>) {
        call.respond(HttpStatusCode.BadRequest, errors)
    }

    override suspend fun success(id: Int) {
        call.respond(id)
    }

    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun notFound(id: Int) {
        call.respond(HttpStatusCode.NotFound, id)
    }

}