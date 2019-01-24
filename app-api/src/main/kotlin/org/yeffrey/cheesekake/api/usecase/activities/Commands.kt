package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCase

interface CreateActivity : UseCase<CreateActivity.Request, ActivityDto> {
    data class Request(val categoryId: Int, val title: String, val summary: String)
}