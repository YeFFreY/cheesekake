package org.yeffrey.cheesekake.web.core.filter

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.yeffrey.cheesekake.api.usecase.Principal
import java.util.*

interface AuthenticationSession {
    fun isAuthenticated(): Boolean
}


interface SessionProvider<T> {
    fun retrieve(key: String): Option<T>
    fun store(key: String, session: T)
}

data class Session(val principal: Option<Principal> = Option.empty()) : AuthenticationSession {
    override fun isAuthenticated(): Boolean {
        return principal.isDefined()
    }
}

/**
 * Leaky store
 */
class InMemorySessionProvider<T> : SessionProvider<T> {
    private val store: MutableMap<String, T> = mutableMapOf()
    override fun retrieve(key: String): Option<T> {
        return Option.fromNullable(store[key])
    }

    override fun store(key: String, session: T) {
        store[key] = session
    }
}

object Sessions {
    private val logger = KotlinLogging.logger {}

    object UseSessions {
        operator fun <T> invoke(cookieName: String, key: RequestContextLens<T>, sessionProvider: SessionProvider<T>, sessionBuilder: () -> T) = Filter { next ->
            { request ->
                val sessionId = Option.fromNullable(request.cookie(cookieName)).map(Cookie::value)
                val session: T = sessionId.flatMap { sessionProvider.retrieve(it) }.getOrElse(sessionBuilder)
                val req = request.with(key of session)

                val response = next(req)

                val sessionToStore: Pair<String, Response> = when (sessionId) {
                    is None -> {
                        val id = UUID.randomUUID().toString()
                        Pair(id, response.cookie(Cookie(cookieName, id).httpOnly().path("/").maxAge(10)))
                    }
                    is Some -> Pair(sessionId.t, response)
                }
                sessionProvider.store(sessionToStore.first, key(req))
                sessionToStore.second
            }
        }
    }

    object FakePrincipal {
        operator fun invoke(key: RequestContextLens<Session>, principalId: Int) = Filter { next ->
            { req ->
                val request = req.with(key of (key(req).copy(principal = Option.just(Principal(principalId)))))
                next(request)
            }
        }
    }
    object Authenticated {
        operator fun <T : AuthenticationSession> invoke(key: RequestContextLens<T>) = Filter { next ->
            { request ->
                val session = key(request)
                if (session.isAuthenticated()) {
                    next(request)
                } else {
                    logger.error("User is NOT authenticated")
                    Response(Status.UNAUTHORIZED)
                }

            }
        }
    }
}
