package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.FormattedTextDto
import org.yeffrey.cheesekake.api.usecase.UseCase

data class ActivityCategoryDto(val id: Int, val name: String, val description: String?)
data class ActivityDto(val id: Int, val title: String, val summary: FormattedTextDto, val category: ActivityCategoryDto)

interface QueryMyActivities : UseCase<QueryMyActivities.Request, List<ActivityDto>> {
    data class Request(val filter: String = "")
}

interface QueryActivity : UseCase<QueryActivity.Request, ActivityDto> {
    data class Request(val activityId: Int)
}