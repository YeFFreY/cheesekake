package org.yeffrey.cheesekake.web.api.activities.categories

import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.activities.ActivityCategoryDto
import org.yeffrey.cheesekake.api.usecase.activities.categories.QueryActivityCategories
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.fetcherPresenter

fun TypeRuntimeWiring.Builder.activityCategoryQueries(queryActivityCategories: QueryActivityCategories) {
    fetcherPresenter<List<ActivityCategoryDto>>("activityCategories") { _, presenter ->
        queryActivityCategories.handle(WebContext(QueryActivityCategories.Request()), presenter)
    }
}