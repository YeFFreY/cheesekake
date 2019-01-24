package org.yeffrey.cheesekake.api.usecase.skills

import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.domain.skills.Skill
import org.yeffrey.cheesekake.domain.skills.SkillQueryByActivitiesGateway
import org.yeffrey.cheesekake.domain.skills.SkillsQueryGateway

fun Skill.toDto(): SkillDto = SkillDto(this.id, this.name, this.description.orNull())

class QueryMySkillsImpl(private val skillsGateway: SkillsQueryGateway) : QueryMySkills {
    override fun handle(context: UseCaseContext<QueryMySkills.Request>, presenter: UseCasePresenter<List<SkillDto>>) {
        presenter.success(skillsGateway.query().map(Skill::toDto))
    }
}

class QuerySkillsByActivitiesImpl(private val skillGateway: SkillQueryByActivitiesGateway) : QuerySkillsByActivities {
    override fun handle(context: UseCaseContext<QuerySkillsByActivities.Request>, presenter: UseCasePresenter<Map<Int, List<SkillDto>>>) {
        presenter.success(skillGateway.query(context.request.activityIds).mapValues {
            it.value.map(Skill::toDto)
        })
    }

}