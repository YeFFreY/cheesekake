package org.yeffrey.cheesekake.web.core.filter

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import java.util.*

interface SessionProvider<T> {
    fun retrieve(key: String): Option<T>
    fun store(key: String, session: T)
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

class SessionFilter<T>(private val cookieName: String, private val key: RequestContextLens<T>, private val sessionProvider: SessionProvider<T>, val sessionBuilder: () -> T) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler {
        return { request ->
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