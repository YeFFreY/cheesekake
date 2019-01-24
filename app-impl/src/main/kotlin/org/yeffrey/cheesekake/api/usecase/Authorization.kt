package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option

fun <D> mustBeAuthenticated(principal: Option<Principal>, presenter: UseCasePresenter<D>, block: (principal: Principal) -> Unit) {
    principal.fold({ presenter.accessDenied() }) { thePrincipal ->
        block(thePrincipal)
    }
}