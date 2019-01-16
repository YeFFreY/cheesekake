package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.domain.activities.Activity

private fun Activity.toDto(): ActivityDto = ActivityDto(this.id, this.title, this.summary)

class QueryMyActivitiesImpl : QueryMyActivities {
    override suspend fun handle(context: UseCaseContext<QueryMyActivities.Request>, presenter: UseCasePresenter) {
        presenter.success(listOf(Activity(1, "bob", "bub").toDto()))
    }
}