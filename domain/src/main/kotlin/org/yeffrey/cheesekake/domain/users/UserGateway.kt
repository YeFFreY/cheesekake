package org.yeffrey.cheesekake.domain.users

import arrow.core.Option
import org.yeffrey.cheesekake.domain.users.entities.User

interface RegisterUserGateway {
    suspend fun register(user: User): Option<Int>
}