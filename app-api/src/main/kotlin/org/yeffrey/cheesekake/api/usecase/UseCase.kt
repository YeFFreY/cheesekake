package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option


interface UseCase<R : UseCaseRequest, P : UseCasePresenter> {
    suspend fun handle(request: R, presenter: P, userId: Option<Int> = Option.empty())
}


abstract class UseCaseRequest

interface UseCasePresenter {
    suspend fun accessDenied()
    suspend fun notFound(id: Int)
}

data class Action(val name: String)
interface Resource {
    val id: Int
    val actions: List<Action>
}