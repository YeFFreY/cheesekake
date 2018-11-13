package org.yeffrey.cheesekake.web

import io.ktor.auth.Principal

data class CheeseKakeSesion(val userId: Int?)
data class CheesePrincipal(val userId: Int) : Principal