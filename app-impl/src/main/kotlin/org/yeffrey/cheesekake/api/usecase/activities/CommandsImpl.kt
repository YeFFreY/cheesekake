package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.None
import arrow.core.Some
import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.activities.ActivityQueryGateway
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGeneralInformationGateway

class CreateActivityImpl(private val activityGateway: CreateActivityGateway, private val queryGateway: ActivityQueryGateway) : CreateActivity {
    override fun handle(context: UseCaseContext<CreateActivity.Request>, presenter: UseCasePresenter<ActivityDto>) = mustBeAuthenticated(context.principal, presenter) {
        val id = activityGateway.create(context.request.categoryId, context.request.title, context.request.summaryFormatted, context.request.summaryRaw, it.id)
        val activity = queryGateway.query(id, it.id)
        when (activity) {
            is None -> presenter.notFound()
            is Some -> presenter.success(activity.t.toDto())
        }
    }
}

class UpdateActivityGeneralInformationImpl(private val activityGateway: UpdateActivityGeneralInformationGateway, private val queryGateway: ActivityQueryGateway) : UpdateActivityGeneralInformation {
    override fun handle(context: UseCaseContext<UpdateActivityGeneralInformation.Request>, presenter: UseCasePresenter<ActivityDto>) = mustBeAuthenticated(context.principal, presenter) {
        val id = activityGateway.updateGeneralInformation(context.request.activityId, context.request.categoryId, context.request.title, context.request.summaryFormatted, context.request.summaryRaw, it.id)
        val activity = queryGateway.query(id, it.id)
        when (activity) {
            is None -> presenter.notFound()
            is Some -> presenter.success(activity.t.toDto())
        }
    }
}