package org.yeffrey.cheesekake.api.usecase.activities

interface QueryActivities {
    suspend fun find(request: Request, presenter: Presenter)
    data class Request(val titleContains: String?)
    interface Presenter {
        data class Activity(val id: Int, val title: String, val summary: String)
        suspend fun success(activities: List<Activity>)
    }
}