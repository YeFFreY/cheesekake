package org.yeffrey.cheesekake.web.api.activities

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.activities.ActivityDto
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.api.usecase.activities.UpdateActivityGeneralInformation
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.core.filter.Session
import org.yeffrey.cheesekake.web.fetcherPresenter

private fun toCreateActivityRequest(arguments: Map<String, Any>): CreateActivity.Request {
    val categoryId = arguments["categoryId"] as Int
    val title = arguments["title"] as String
    val summaryFormatted = arguments["summaryFormatted"] as String
    val summaryRaw = arguments["summaryRaw"] as String
    return CreateActivity.Request(categoryId, title, summaryFormatted, summaryRaw)
}

private fun toUpdateActivityGeneralInformationRequest(arguments: Map<String, Any>): UpdateActivityGeneralInformation.Request {
    val mapper = jacksonObjectMapper()
    return mapper.convertValue(arguments, UpdateActivityGeneralInformation.Request::class.java)
}

fun TypeRuntimeWiring.Builder.activityMutations(createActivity: CreateActivity, updateActivityGeneralInformation: UpdateActivityGeneralInformation) {
    fetcherPresenter<ActivityDto>("createActivity") { dfe, presenter ->
        val principal = dfe.getContext<Session>().principal
        createActivity.handle(WebContext(toCreateActivityRequest(dfe.arguments), principal), presenter)
    }
    fetcherPresenter<ActivityDto>("updateActivityGeneralInformation") { dfe, presenter ->
        val principal = dfe.getContext<Session>().principal
        updateActivityGeneralInformation.handle(WebContext(toUpdateActivityGeneralInformationRequest(dfe.arguments), principal), presenter)
    }
}