package org.yeffrey.cheesekake.web.api.activities

import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.activities.ActivityDto
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivity
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivities
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.fetcherPresenter
import org.yeffrey.cheesekake.web.schema.Activity
import org.yeffrey.cheesekake.web.schema.ActivityMetadata
import org.yeffrey.cheesekake.web.schema.MinMax


val activities = mutableListOf(
        Activity(1, "bob", "theBob", ActivityMetadata(MinMax(5, 20), MinMax(3, 7), MinMax(3, 5))),
        Activity(2, "bob 2", "theBob 2", null),
        Activity(3, "bob 3", "theBob 3", ActivityMetadata(MinMax(20, 50), null, MinMax(5, 10)))
)


fun TypeRuntimeWiring.Builder.activityQueries(queryMyActivities: QueryMyActivities, queryActivity: QueryActivity) {
    fetcherPresenter<List<ActivityDto>>("activities") { _, presenter ->
        queryMyActivities.handle(WebContext(QueryMyActivities.Request()), presenter)
    }
    fetcherPresenter<ActivityDto>("activity") { dfe, presenter ->
        val id: Int = (dfe.arguments["id"] as String).toInt()
        queryActivity.handle(WebContext(QueryActivity.Request(id)), presenter)
    }
}