package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest

interface AddResource : UseCase<AddResource.Request, AddResource.Presenter> {
    data class Request(val activityId: Int, val resourceId: Int, val quantity: Int) : UseCaseRequest()
    interface Presenter : UseCasePresenter {
        suspend fun success(id: Int)
    }
}