package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase

interface CreateActivity : UseCase<CreateActivity.Request, ActivityDto> {
    data class Request(val categoryId: Int, val title: String, val summaryFormatted: String, val summaryRaw: String)
}

interface UpdateActivityGeneralInformation : UseCase<UpdateActivityGeneralInformation.Request, ActivityDto> {
    data class Request(val activityId: Int, val categoryId: Int, val title: String, val summaryFormatted: String, val summaryRaw: String)
}