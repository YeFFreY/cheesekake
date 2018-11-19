package org.yeffrey.cheesekake.web

import arrow.core.Option
import io.ktor.application.ApplicationCall
import io.ktor.auth.Principal
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.core.error.ErrorDescription

data class CheeseKakeSesion(val userId: Int?)
data class CheesePrincipal(val userId: Int) : Principal
data class CheeseError(val message: String)

fun withPrincipalId(call: ApplicationCall): Option<Int> = Option.fromNullable(call.principal<CheesePrincipal>()?.userId)

data class WebAction(val action: String, val link: String)
data class WebResource(val data: Any, val links: List<WebAction> = emptyList())

interface WebPresenter : UseCasePresenter {
    val call: ApplicationCall
    override suspend fun accessDenied() {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "access denied"))
    }

    override suspend fun notFound(id: Int) {
        call.respond(HttpStatusCode.NotFound, id)
    }

    override suspend fun validationFailed(errors: List<ErrorDescription>) {
        call.respond(HttpStatusCode.BadRequest, errors)
    }

}