package org.yeffrey.cheesekake.web.api.users

import mu.KotlinLogging
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.users.LoginUser
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.core.error.ErrorDescription

class UsersHandlerImpl(private val loginUser: LoginUser) {
    private val logger = KotlinLogging.logger {}

    private val sessionRoutes = routes(
            "/session" bind Method.POST to login()
    )

    operator fun invoke(): RoutingHttpHandler =
            ServerFilters.CatchLensFailure {
                logger.error("Error during body parsing : ${it.failures}", it.cause)
                Response(Status.BAD_REQUEST)
            }.then(routes(
                    "/users" bind routes(
                            sessionRoutes
                    )
            ))

    private val loginLens = Body.auto<LoginUser.Request>().toLens()

    private fun login() = { req: Request ->
        val presenter = NoOpPresenter()
        loginUser.handle(WebContext(loginLens(req)), presenter)
        presenter.present()
    }
}

class NoOpPresenter : UseCasePresenter<Unit> {
    private lateinit var viewModel: Response
    private val errorsLens = Body.auto<List<ErrorDescription>>().toLens()

    override fun fail(errors: List<ErrorDescription>) {
        viewModel = errorsLens(errors, Response(Status.BAD_REQUEST))
    }

    override fun success(data: Unit) {
        viewModel = Response(Status.OK)
    }

    override fun notFound() {
        viewModel = Response(Status.NOT_FOUND)
    }

    fun present(): Response {
        return viewModel
    }
}