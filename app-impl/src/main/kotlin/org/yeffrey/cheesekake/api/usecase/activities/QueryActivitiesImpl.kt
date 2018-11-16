package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Option
import arrow.core.toOption
import org.yeffrey.cheesekake.api.usecase.Action
import org.yeffrey.cheesekake.domain.activities.QueryActivitiesGateway
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummaryProjection

class QueryActivitiesImpl(private val activitiesGateway: QueryActivitiesGateway) : QueryActivities {
    override suspend fun handle(request: QueryActivities.Request, presenter: QueryActivities.Presenter, userId: Option<Int>) {
        val result = activitiesGateway.query(QueryActivitiesGateway.ActivityQueryCriteria(request.titleContains.toOption()))
        presenter.success(result.map { activity ->
            val actions = activity.actions.filter {
                it.value(activity, userId)
            }.map { Action(it.key) }
            activity.toPresenterModel(actions)
        })
    }
}

fun ActivitySummaryProjection.toPresenterModel(actions: List<Action>): QueryActivities.Presenter.Activity {
    return QueryActivities.Presenter.Activity(this.id, this.title, this.summary, actions)
}