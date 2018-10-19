package org.yeffrey.cheesekake.api.usecase

fun isAuthenticated(request: UseCaseRequest): Boolean {
    return request.userId.nonEmpty()
}


suspend fun mustBeAuthenticated(request: UseCaseRequest, presenter: UseCasePresenter, block: suspend () -> Unit) {
    if (isAuthenticated(request)) {
        block()
    } else {
        presenter.accessDenied()
    }
}