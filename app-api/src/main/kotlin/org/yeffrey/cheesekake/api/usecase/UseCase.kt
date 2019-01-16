package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option
import org.yeffrey.core.error.ErrorDescription


interface UseCase<R> {
    suspend fun handle(context: UseCaseContext<R>, presenter: UseCasePresenter)
}

interface UseCaseContext<R> {
    val request: R
    val principal: Option<Principal>
}

interface Principal {
    val id: Int
}
interface UseCasePresenter {
    suspend fun fail(errors: List<ErrorDescription>)
    suspend fun success(data: Any)
}


