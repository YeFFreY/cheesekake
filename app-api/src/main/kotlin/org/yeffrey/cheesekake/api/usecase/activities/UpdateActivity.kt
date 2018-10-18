package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.ValidationError

interface UpdateActivity : UseCase<UpdateActivity.Request, UpdateActivity.Presenter> {
    data class Request(val activityId: Int, val title: String, val summary: String): UseCaseRequest()
    interface Presenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun success(id: Int)
        suspend fun notFound(id: Int)
    }
}