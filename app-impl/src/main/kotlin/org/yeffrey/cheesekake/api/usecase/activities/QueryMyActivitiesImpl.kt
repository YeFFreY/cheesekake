package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.domain.activities.Activity
import org.yeffrey.cheesekake.domain.activities.ActivityQueryGateway

private fun Activity.toDto(): ActivityDto = ActivityDto(this.id, this.title, this.summary)

class QueryMyActivitiesImpl(private val activitiesGateway: ActivityQueryGateway) : QueryMyActivities {
    override suspend fun handle(context: UseCaseContext<QueryMyActivities.Request>, presenter: UseCasePresenter) {
        val activities = activitiesGateway.query().map { activity -> activity.toDto() }
        presenter.success(activities)
    }
}