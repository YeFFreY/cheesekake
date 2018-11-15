package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.core.error.ErrorDescription

interface CreateActivity : UseCase<CreateActivity.Request, CreateActivity.Presenter> {
    data class Request(val user: Option<Int>, val title: String, val summary: String) : UseCaseRequest(user)
    interface Presenter : UseCasePresenter {
        suspend fun validationFailed(errors: List<ErrorDescription>)
        suspend fun success(id: Int)
    }
}