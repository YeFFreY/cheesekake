package org.yeffrey.cheesekake.api.usecase.activities.categories

import org.yeffrey.cheesekake.api.usecase.UseCase
import org.yeffrey.cheesekake.api.usecase.activities.ActivityCategoryDto

interface QueryActivityCategories : UseCase<QueryActivityCategories.Request, List<ActivityCategoryDto>> {
    data class Request(val filter: String = "")
}
