package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.RemoveResource
import org.yeffrey.cheesekake.web.WebPresenter

data class RemoveResourceDto(val resourceId: Int = -1)

fun RemoveResourceDto.toRequest(activityId: Int): RemoveResource.Request = RemoveResource.Request(activityId, resourceId)

class RemoveResourcePresenter(override val call: ApplicationCall) : RemoveResource.Presenter, WebPresenter {
    override suspend fun success() {
        call.respond(HttpStatusCode.OK)
    }
}