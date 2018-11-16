package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest

interface RemoveResource : UseCase<RemoveResource.Request, RemoveResource.Presenter> {
    data class Request(val activityId: Int, val resourceId: Int) : UseCaseRequest()
    interface Presenter : UseCasePresenter {
        suspend fun success()
    }
}
