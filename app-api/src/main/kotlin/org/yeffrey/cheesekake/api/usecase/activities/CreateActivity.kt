package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.ValidationError

interface CreateActivity {
    suspend fun create(request: Request, presenter: Presenter)
    data class Request(val title: String, val summary: String): UseCaseRequest() {
        override fun allow() : Boolean {
            return this.userId.nonEmpty()
        }
    }
    interface Presenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun accessDenied()
        suspend fun success(id: Int)
    }
}