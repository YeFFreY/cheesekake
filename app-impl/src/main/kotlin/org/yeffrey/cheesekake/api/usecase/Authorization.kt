package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option
import org.yeffrey.cheesekake.domain.users.entities.UserId

suspend fun mustBeAuthenticated(userId: Option<Int>, presenter: UseCasePresenter, block: suspend (userId: UserId) -> Unit) {
    userId.fold({ presenter.accessDenied() }) { theUserId ->
        block(theUserId)
    }
}
