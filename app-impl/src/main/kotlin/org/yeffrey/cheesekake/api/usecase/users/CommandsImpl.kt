package org.yeffrey.cheesekake.api.usecase.users

import arrow.core.None
import arrow.core.Some
import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.domain.users.UserLoginGateway

class LoginUserImpl(private val userGateway: UserLoginGateway) : LoginUser {
    override fun handle(context: UseCaseContext<LoginUser.Request>, presenter: UseCasePresenter<Unit>) {
        val userId = userGateway.login(context.request.email, context.request.password)
        when (userId) {
            is None -> presenter.notFound()
            is Some -> presenter.success(Unit)
        }
    }

}