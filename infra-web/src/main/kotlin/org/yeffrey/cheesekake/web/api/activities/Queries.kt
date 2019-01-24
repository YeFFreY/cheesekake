package org.yeffrey.cheesekake.web.api.activities

import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.activities.ActivityDto
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivity
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivities
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.core.filter.Session
import org.yeffrey.cheesekake.web.fetcherPresenter

fun TypeRuntimeWiring.Builder.activityQueries(queryMyActivities: QueryMyActivities, queryActivity: QueryActivity) {
    fetcherPresenter<List<ActivityDto>>("activities") { _, presenter ->
        queryMyActivities.handle(WebContext(QueryMyActivities.Request()), presenter)
    }
    fetcherPresenter<ActivityDto>("activity") { dfe, presenter ->
        val id: Int = (dfe.arguments["id"] as String).toInt()
        val principal = dfe.getContext<Session>().principal
        queryActivity.handle(WebContext(QueryActivity.Request(id), principal), presenter)
    }
}