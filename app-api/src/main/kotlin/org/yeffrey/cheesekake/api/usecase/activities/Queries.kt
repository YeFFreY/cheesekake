package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase

data class ActivityDto(val id: Int, val title: String, val summary: String)
interface QueryMyActivities : UseCase<QueryMyActivities.Request, List<ActivityDto>> {
    data class Request(val filter: String = "")
}

interface QueryActivity : UseCase<QueryActivity.Request, ActivityDto> {
    data class Request(val activityId: Int)
}