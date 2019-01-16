package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase

data class ActivityDto(val id: Int, val title: String, val summary: String)
interface QueryMyActivities : UseCase<QueryMyActivities.Request> {
    data class Request(val filter: String = "")
}