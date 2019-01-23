package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.None
import arrow.core.Some
import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.domain.activities.ActivityQueryGateway
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway

class CreateActivityImpl(private val activityGateway: CreateActivityGateway, private val queryGateway: ActivityQueryGateway) : CreateActivity {
    override fun handle(context: UseCaseContext<CreateActivity.Request>, presenter: UseCasePresenter<ActivityDto>) {
        val id = activityGateway.create(context.request.categoryId, context.request.title, context.request.summary)
        val activity = queryGateway.query(id)
        when (activity) {
            is None -> presenter.notFound()
            is Some -> presenter.success(activity.t.toDto())
        }
    }

}