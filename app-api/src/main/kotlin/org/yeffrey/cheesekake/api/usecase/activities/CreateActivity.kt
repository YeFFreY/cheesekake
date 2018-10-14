package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.domain.ValidationError

interface CreateActivity {
    suspend fun create(request: Request, presenter: Presenter)
    data class Request(val title: String, val summary: String)
    interface Presenter {
        suspend fun validationFailed(errors: List<ValidationError>)
        suspend fun success(id: Int)
    }
}