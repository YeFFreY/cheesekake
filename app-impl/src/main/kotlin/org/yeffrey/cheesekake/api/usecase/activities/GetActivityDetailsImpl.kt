package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.query.ActivityDetailsProjection

class GetActivityDetailsImpl(private val activityGateway: QueryActivityGateway) : GetActivityDetails {
    override suspend fun handle(request: GetActivityDetails.Request, presenter: GetActivityDetails.Presenter) {
        activityGateway.get(request.id).fold({ presenter.notFound(request.id) }) {
            presenter.success(it.toPresenterModel())
        }
    }
}

fun ActivityDetailsProjection.toPresenterModel(): GetActivityDetails.Presenter.ActivityDetails {
    val resources = this.resources.map {
        GetActivityDetails.Presenter.ActivityResource(it.resourceId, it.quantity)
    }
    return GetActivityDetails.Presenter.ActivityDetails(this.id, this.title, this.summary, resources, this.skills)
}