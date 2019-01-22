package org.yeffrey.cheesekake.web.core.filter

import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens


interface AuthenticationSession {
    fun isAuthenticated(): Boolean
}

object AuthenticationFilter {
    object Authenticated {
        operator fun <T : AuthenticationSession> invoke(key: RequestContextLens<T>) = Filter { next ->
            { request ->
                val session = key(request)
                if (session.isAuthenticated()) {
                    next(request)
                } else {
                    Response(Status.UNAUTHORIZED)
                }

            }
        }
    }
}