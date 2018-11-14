package org.yeffrey.cheesekake.api.usecase

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.yeffrey.cheesekake.domain.Policy
import org.yeffrey.cheesekake.domain.respect


abstract class UseCase<R : UseCaseRequest, P : UseCasePresenter, D>(val requiredPolicies: List<Policy<D>> = emptyList()) {
    //val requiredPolicies: List<Policy<D>> = emptyList()
    suspend fun handle(request: R, presenter: P) {
        val d = retrieveAggregate(request, presenter)
        when (d) {
            is None -> presenter.notFound(0)
            is Some -> process(d.t, request, presenter)
        }
    }

    private suspend fun process(aggregate: D, request: R, presenter: P) {
        val userId = request.userId
        val policiesRespected = when (userId) {
            is None -> false
            is Some -> respect(userId.t, aggregate, requiredPolicies)
        }
        if (policiesRespected) {
            perform(aggregate, request, presenter)
        } else {
            presenter.accessDenied()
        }

    }

    abstract suspend fun retrieveAggregate(request: R, presenter: P): Option<D>
    abstract suspend fun perform(aggregate: D, request: R, presenter: P)
}


abstract class UseCaseRequest(open val userId: Option<Int> = Option.empty())

interface UseCasePresenter {
    suspend fun accessDenied()
    suspend fun notFound(id: Int)
}