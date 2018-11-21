package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option
import org.yeffrey.core.error.ErrorDescription


interface UseCase<R : UseCaseRequest, P : UseCasePresenter> {
    suspend fun handle(request: R, presenter: P, userId: Option<Int> = Option.empty())
}


abstract class UseCaseRequest

interface UseCasePresenter {
    suspend fun validationFailed(errors: List<ErrorDescription>)
    suspend fun accessDenied()
    suspend fun notFound(id: Int)
}


interface Resource {
    data class Action(val name: String)
    val id: Int
    val actions: List<Action>
}