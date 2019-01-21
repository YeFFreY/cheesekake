package org.yeffrey.cheesekake.web.core

import arrow.core.Option
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import org.http4k.lens.RequestContextLens


interface SessionProvider<T> {
    fun retrieve(key: String): Option<T>
}

class InMemorySessionProvider<T> : SessionProvider<T> {
    val store: MutableMap<String, T> = mutableMapOf()
    override fun retrieve(key: String): Option<T> {
        return Option.fromNullable(store[key])
    }
}

class SessionFilter<T>(private val key: RequestContextLens<T>, private val sessionProvider: SessionProvider<T>, val sessionBuilder: () -> T) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler {
        return { request ->
            val req: Request = Option.fromNullable(request.cookie("CK_SESSION"))
                    .fold({ request.with(key of sessionBuilder()) }) { cookie ->
                        val session: Option<T> = sessionProvider.retrieve(cookie.value)
                        session.fold({ request }) { request.with(key of it) }
                    }
            println(key(req))
            val response = next(req)
            println(key(req))
            response

        }
    }

}