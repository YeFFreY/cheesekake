package org.yeffrey.cheesekake.api.usecase.skills

import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.domain.skills.Skill
import org.yeffrey.cheesekake.domain.skills.SkillQueryByActivitiesGateway
import org.yeffrey.cheesekake.domain.skills.SkillQueryGateway

private fun Skill.toDto(): SkillDto = SkillDto(this.id, this.name, this.description)
class QueryMySkillsImpl(private val skillGateway: SkillQueryGateway) : QueryMySkills {
    override fun handle(context: UseCaseContext<QueryMySkills.Request>, presenter: UseCasePresenter<List<SkillDto>>) {
        presenter.success(skillGateway.query().map(Skill::toDto))
    }
}

class QuerySkillsByActivitiesImpl(private val skillGateway: SkillQueryByActivitiesGateway) : QuerySkillsByActivities {
    override fun handle(context: UseCaseContext<QuerySkillsByActivities.Request>, presenter: UseCasePresenter<Map<Int, List<SkillDto>>>) {
        presenter.success(skillGateway.query(context.request.activityIds).mapValues {
            it.value.map(Skill::toDto)
        })
    }

}