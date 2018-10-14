package org.yeffrey.cheesekake.api.usecase.users

import arrow.core.Some
import arrow.data.*
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.users.RegisterUserGateway
import org.yeffrey.cheesekake.domain.users.command.Registration
import org.yeffrey.cheesekake.domain.users.command.toPassword
import org.yeffrey.cheesekake.domain.users.command.toUsername

class RegisterUserImpl(private val userGateway: RegisterUserGateway) : RegisterUser {
    override suspend fun register(request: RegisterUser.Request, presenter: RegisterUser.Presenter) {
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

fun RegisterUser.Request.toDomain() : ValidatedNel<ValidationError, Registration> {
    return ValidatedNel.applicative<Nel<ValidationError>>(Nel.semigroup()).map(
            this.username.toUsername(),
            this.password.toPassword()
    ) {(username, password) ->
        Registration(username, password)
    }.fix()
}