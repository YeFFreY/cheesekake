package org.yeffrey.cheesekake.web.activities

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.web.WebPresenter

data class CreateActivityDto(val title: String = "", val summary: String = "")

fun CreateActivityDto.toRequest(): CreateActivity.Request = CreateActivity.Request(this.title, this.summary)

class CreateActivityPresenter(override val call: ApplicationCall) : CreateActivity.Presenter, WebPresenter {
    override suspend fun success(id: Int) {
        call.respond(id)
    }

}