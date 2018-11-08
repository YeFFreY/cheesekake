package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.toOption
import org.yeffrey.cheesekake.domain.activities.QueryActivitiesGateway
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummaryProjection

class QueryActivitiesImpl(private val activitiesGateway: QueryActivitiesGateway) : QueryActivities {
    override suspend fun handle(request: QueryActivities.Request, presenter: QueryActivities.Presenter) {
        val result = activitiesGateway.query(QueryActivitiesGateway.ActivityQueryCriteria(request.titleContains.toOption()))
        presenter.success(result.map { it.toPresenterModel() })
    }
}

fun ActivitySummaryProjection.toPresenterModel(): QueryActivities.Presenter.Activity {
    return QueryActivities.Presenter.Activity(this.id, this.title, this.summary)
}