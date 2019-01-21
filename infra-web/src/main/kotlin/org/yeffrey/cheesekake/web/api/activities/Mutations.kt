package org.yeffrey.cheesekake.web.api.activities

import arrow.core.Option
import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.activities.ActivityDto
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.fetcherPresenter


fun TypeRuntimeWiring.Builder.activityMutations(createActivity: CreateActivity) {
    fetcherPresenter<ActivityDto>("createActivity") { dfe, presenter ->
        val categoryId = dfe.arguments["categoryId"] as Int
        val title = dfe.arguments["title"] as String
        val summary = Option.fromNullable(dfe.arguments["summary"] as String)
        createActivity.handle(WebContext(CreateActivity.Request(categoryId, title, summary)), presenter)
    }
}