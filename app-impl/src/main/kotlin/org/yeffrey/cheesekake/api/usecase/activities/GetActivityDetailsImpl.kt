package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.query.ActivityDetails

class GetActivityDetailsImpl(private val activityGateway: QueryActivityGateway) : GetActivityDetails {
    override suspend fun handle(request: GetActivityDetails.Request, presenter: GetActivityDetails.Presenter) {
        activityGateway.get(request.id).fold({ presenter.notFound(request.id) }) {
            presenter.success(it.toPresenterModel())
        }
    }
}

fun ActivityDetails.toPresenterModel(): GetActivityDetails.Presenter.ActivityDetails {
    return GetActivityDetails.Presenter.ActivityDetails(this.id, this.title, this.summary, this.resources, this.skills)
}