package org.yeffrey.cheesekake.web

import arrow.core.Option
import io.ktor.application.ApplicationCall
import io.ktor.auth.Principal
import io.ktor.auth.principal

data class CheeseKakeSesion(val userId: Int?)
data class CheesePrincipal(val userId: Int) : Principal


fun withPrincipalId(call: ApplicationCall): Option<Int> = Option.fromNullable(call.principal<CheesePrincipal>()?.userId)