package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.*

interface QueryActivities : UseCase<QueryActivities.Request, QueryActivities.Presenter> {
    data class Request(val titleContains: String?): UseCaseRequest()
    interface Presenter : UseCasePresenter {
        data class Activity(val id: Int, val title: String, val summary: String, override val actions: List<Action>) : Resource
        suspend fun success(activities: List<Activity>)
    }
}