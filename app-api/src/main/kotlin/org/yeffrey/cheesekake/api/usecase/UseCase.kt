package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option
import org.yeffrey.core.error.ErrorDescription


interface UseCase<R, D> {
    fun handle(context: UseCaseContext<R>, presenter: UseCasePresenter<D>)
}

interface UseCaseContext<R> {
    val request: R
    val principal: Option<Principal>
}

data class Principal(val id: Int)

interface UseCasePresenter<D> {
    fun fail(errors: List<ErrorDescription>)
    fun success(data: D)
    fun notFound()
    fun accessDenied()
}


