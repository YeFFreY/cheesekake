package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.UseCaseRequest

interface QueryActivities : UseCase<QueryActivities.Request, QueryActivities.Presenter> {
    data class Request(val titleContains: String?): UseCaseRequest()
    interface Presenter {
        data class Activity(val id: Int, val title: String, val summary: String)
        suspend fun success(activities: List<Activity>)
    }
}