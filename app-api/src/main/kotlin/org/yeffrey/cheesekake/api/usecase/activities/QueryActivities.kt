package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest
import org.yeffrey.cheesekake.domain.activities.entities.Activity

interface QueryActivities : UseCase<QueryActivities.Request, QueryActivities.Presenter, Activity> {
    data class Request(val titleContains: String?): UseCaseRequest()
    interface Presenter : UseCasePresenter {
        data class Activity(val id: Int, val title: String, val summary: String)
        suspend fun success(activities: List<Activity>)
    }
}