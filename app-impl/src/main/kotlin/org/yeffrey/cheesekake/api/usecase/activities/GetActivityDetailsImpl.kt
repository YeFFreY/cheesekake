package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.Action
import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.query.ActivityDetailsProjection

class GetActivityDetailsImpl(private val activityGateway: QueryActivityGateway) : GetActivityDetails {
    override suspend fun handle(request: GetActivityDetails.Request, presenter: GetActivityDetails.Presenter, userId: Option<Int>) {
        activityGateway.get(request.id).fold({ presenter.notFound(request.id) }) { activity ->
            val actions = activity.actions.filter {
                it.value(activity, userId)
            }.map { Action(it.key) }
            presenter.success(activity.toPresenterModel(actions))
        }
    }
}

fun ActivityDetailsProjection.toPresenterModel(actions: List<Action>): GetActivityDetails.Presenter.ActivityDetails {
    val resources = this.resources.map {
        GetActivityDetails.Presenter.ActivityResource(it.resourceId, it.quantity)
    }
    return GetActivityDetails.Presenter.ActivityDetails(this.id, this.title, this.summary, resources, this.skills, actions)
}