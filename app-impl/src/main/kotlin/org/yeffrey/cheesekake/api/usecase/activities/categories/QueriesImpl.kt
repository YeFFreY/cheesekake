package org.yeffrey.cheesekake.api.usecase.activities.categories

import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.activities.ActivityCategoryDto
import org.yeffrey.cheesekake.domain.activities.ActivityCategoriesQueryGateway
import org.yeffrey.cheesekake.domain.activities.ActivityCategory

fun ActivityCategory.toDto(): ActivityCategoryDto = ActivityCategoryDto(this.id, this.name, this.description.orNull())

class QueryActivityCategoriesImpl(private val categoryGateway: ActivityCategoriesQueryGateway) : QueryActivityCategories {
    override fun handle(context: UseCaseContext<QueryActivityCategories.Request>, presenter: UseCasePresenter<List<ActivityCategoryDto>>) {
        val categories = this.categoryGateway.query().map { category -> category.toDto() }
        presenter.success(categories)
    }

}