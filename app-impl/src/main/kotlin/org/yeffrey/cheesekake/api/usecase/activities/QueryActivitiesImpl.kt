package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.toOption
import org.yeffrey.cheesekake.domain.activities.ActivityQueryCriteria
import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary

class QueryActivitiesImpl(private val activityGateway: QueryActivityGateway) : QueryActivities {
    override suspend fun find(request: QueryActivities.Request, presenter: QueryActivities.Presenter) {
        val result = activityGateway.query(ActivityQueryCriteria(request.titleContains.toOption()))
        presenter.success(result.map{ it.toPresenterModel() })
    }
}

fun ActivitySummary.toPresenterModel(): QueryActivities.Presenter.Activity {
    return QueryActivities.Presenter.Activity(this.id, this.title, this.summary)
}