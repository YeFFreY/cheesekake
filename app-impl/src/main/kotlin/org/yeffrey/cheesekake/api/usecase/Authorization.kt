package org.yeffrey.cheesekake.api.usecase

import org.yeffrey.cheesekake.domain.users.entities.UserId

suspend fun mustBeAuthenticated(request: UseCaseRequest, presenter: UseCasePresenter, block: suspend (userId: UserId) -> Unit) {
    request.userId.fold( {presenter.accessDenied() }) { userId ->
        block(userId)
    }
}