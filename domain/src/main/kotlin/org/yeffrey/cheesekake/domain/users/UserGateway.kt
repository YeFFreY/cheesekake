package org.yeffrey.cheesekake.domain.users

import arrow.core.Option
import org.yeffrey.cheesekake.domain.users.command.Registration

interface RegisterUserGateway {
    suspend fun register(registration: Registration): Option<Int>
}