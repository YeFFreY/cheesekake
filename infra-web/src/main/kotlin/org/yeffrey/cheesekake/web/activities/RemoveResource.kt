package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.RemoveResource
import org.yeffrey.core.error.ErrorDescription

data class RemoveResourceDto(val resourceId: Int = -1)

fun RemoveResourceDto.toRequest(activityId: Int): RemoveResource.Request = RemoveResource.Request(activityId, resourceId)

class RemoveResourcePresenter(private val call: ApplicationCall) : RemoveResource.Presenter {
    override suspend fun validationFailed(errors: List<ErrorDescription>) {
        call.respond(HttpStatusCode.BadRequest, errors)
    }

    override suspend fun success() {
        call.respond(HttpStatusCode.OK)
    }

    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun notFound(id: Int) {
        call.respond(HttpStatusCode.NotFound, id)
    }

}