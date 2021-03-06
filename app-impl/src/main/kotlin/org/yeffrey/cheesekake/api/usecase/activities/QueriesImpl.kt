package org.yeffrey.cheesekake.api.usecase.activities

import org.yeffrey.cheesekake.api.usecase.FormattedTextDto
import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.activities.ActivitiesQueryGateway
import org.yeffrey.cheesekake.domain.activities.Activity
import org.yeffrey.cheesekake.domain.activities.ActivityQueryGateway

fun Activity.toDto(): ActivityDto = ActivityDto(
        this.id, this.title,
        FormattedTextDto(this.summary.formatted, this.summary.raw),
        ActivityCategoryDto(this.category.id, this.category.name, this.category.description.orNull())
)

class QueryMyActivitiesImpl(private val activitiesGateway: ActivitiesQueryGateway) : QueryMyActivities {
    override fun handle(context: UseCaseContext<QueryMyActivities.Request>, presenter: UseCasePresenter<List<ActivityDto>>) {
        val activities = activitiesGateway.query().map { activity -> activity.toDto() }
        presenter.success(activities)
    }
}

class QueryActivityImpl(private val activitiesGateway: ActivityQueryGateway) : QueryActivity {
    override fun handle(context: UseCaseContext<QueryActivity.Request>, presenter: UseCasePresenter<ActivityDto>) = mustBeAuthenticated(context.principal, presenter) { principal ->
        activitiesGateway.query(context.request.activityId, principal.id).fold({ presenter.notFound() }) {
            presenter.success(it.toDto())
        }
    }
}