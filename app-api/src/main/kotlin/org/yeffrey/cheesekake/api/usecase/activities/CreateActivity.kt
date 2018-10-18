package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.ValidationError

interface CreateActivity: UseCase<CreateActivity.Request, CreateActivity.Presenter> {
    data class Request(val title: String, val summary: String): UseCaseRequest()
    interface Presenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun accessDenied()
        suspend fun success(id: Int)
    }
}