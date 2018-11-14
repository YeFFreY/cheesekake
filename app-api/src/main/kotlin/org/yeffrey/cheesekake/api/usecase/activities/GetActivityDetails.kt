package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.activities.entities.Activity

interface GetActivityDetails : UseCase<GetActivityDetails.Request, GetActivityDetails.Presenter, Activity> {
    data class Request(val id: Int) : UseCaseRequest()
    interface Presenter : UseCasePresenter {
        data class ActivityDetails(val id: Int, val title: String, val summary: String, val resources: List<ActivityResource>, val skills: List<Int>)
        data class ActivityResource(val resourceId: Int, val quantity: Int)

        suspend fun success(activity: ActivityDetails)
    }
}