package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option

interface UseCase<R : UseCaseRequest, P : UseCasePresenter> {
    suspend fun handle(request: R, presenter: P)
}


abstract class UseCaseRequest {
    var userId : Option<Int> = Option.empty()
}

interface UseCasePresenter {
    suspend fun accessDenied()
}