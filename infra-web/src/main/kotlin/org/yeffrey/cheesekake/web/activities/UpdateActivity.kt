package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.UpdateActivity
import org.yeffrey.cheesekake.web.WebPresenter


data class UpdateActivityDto(val title: String = "", val summary: String = "")

fun UpdateActivityDto.toRequest(activityId: Int): UpdateActivity.Request = UpdateActivity.Request(activityId, this.title, this.summary)

class UpdateActivityPresenter(override val call: ApplicationCall) : UpdateActivity.Presenter, WebPresenter {
    override suspend fun success(id: Int) {
        call.respond(id)
    }
}