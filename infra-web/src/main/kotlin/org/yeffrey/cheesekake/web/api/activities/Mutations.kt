package org.yeffrey.cheesekake.web.api.activities

import arrow.core.Option
import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.activities.ActivityDto
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.core.filter.Session
import org.yeffrey.cheesekake.web.fetcherPresenter

private fun from(arguments: Map<String, Any>): CreateActivity.Request {
    val categoryId = arguments["categoryId"] as Int
    val title = arguments["title"] as String
    val summary = Option.fromNullable(arguments["summary"] as String)
    return CreateActivity.Request(categoryId, title, summary)
}

fun TypeRuntimeWiring.Builder.activityMutations(createActivity: CreateActivity) {
    fetcherPresenter<ActivityDto>("createActivity") { dfe, presenter ->
        val principal = dfe.getContext<Session>().principal
        createActivity.handle(WebContext(from(dfe.arguments), principal), presenter)
    }
}