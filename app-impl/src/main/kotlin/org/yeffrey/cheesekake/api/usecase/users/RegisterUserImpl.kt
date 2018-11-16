package org.yeffrey.cheesekake.api.usecase.users

import arrow.core.Option
import arrow.core.Some
import arrow.data.*
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.validated.applicative.applicative
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.users.RegisterUserGateway
import org.yeffrey.cheesekake.domain.users.entities.Credentials
import org.yeffrey.cheesekake.domain.users.entities.User
import org.yeffrey.cheesekake.domain.users.entities.toPassword
import org.yeffrey.cheesekake.domain.users.entities.toUsername

class RegisterUserImpl(private val userGateway: RegisterUserGateway) : RegisterUser {
    override suspend fun handle(request: RegisterUser.Request, presenter: RegisterUser.Presenter, userId: Option<Int>) {
        val registration = request.toDomain()
        when (registration) {
            is Valid -> {
                val id = userGateway.register(registration.a)
                when (id) {
                    is Some -> presenter.success(id.t)
                    else -> presenter.validationFailed(listOf(ValidationError.InvalidUsername))
                }
            }
            is Invalid -> presenter.validationFailed(registration.e.all)
        }

    }
}

fun RegisterUser.Request.toDomain() : ValidatedNel<ValidationError, User> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            this.username.toUsername(),
            this.password.toPassword()
    ) {(username, password) ->
        User.new(Credentials(username, password))
    }.fix()
}