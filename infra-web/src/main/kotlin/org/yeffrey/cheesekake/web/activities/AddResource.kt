package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.AddResource
import org.yeffrey.cheesekake.web.WebPresenter

data class AddResourceDto(val resourceId: Int = -1, val quantity: Int = -1)

fun AddResourceDto.toRequest(activityId: Int): AddResource.Request = AddResource.Request(activityId, resourceId, quantity)

class AddResourcePresenter(override val call: ApplicationCall) : AddResource.Presenter, WebPresenter {
    override suspend fun success(id: Int) {
        call.respond(id)
    }
}