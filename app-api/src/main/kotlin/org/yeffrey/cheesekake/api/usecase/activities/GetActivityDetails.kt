package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.Resource
import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest

interface GetActivityDetails : UseCase<GetActivityDetails.Request, GetActivityDetails.Presenter> {
    data class Request(val id: Int) : UseCaseRequest()
    interface Presenter : UseCasePresenter {
        data class ActivityDetails(override val id: Int, val title: String, val summary: String, val resources: List<ActivityResource>, val skills: List<Int>, override val actions: List<Resource.Action>) : Resource
        data class ActivityResource(val resourceId: Int, val quantity: Int)

        suspend fun success(activity: ActivityDetails)
    }
}