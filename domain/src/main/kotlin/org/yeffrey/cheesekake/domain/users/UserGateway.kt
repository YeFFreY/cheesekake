package org.yeffrey.cheesekake.domain.users

import arrow.core.Option

interface UserLoginGateway {
    fun login(email: String, password: String): Option<Int>
}