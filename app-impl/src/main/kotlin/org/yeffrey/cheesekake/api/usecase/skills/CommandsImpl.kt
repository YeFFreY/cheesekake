package org.yeffrey.cheesekake.api.usecase.skills

import arrow.core.None
import arrow.core.Some
import org.yeffrey.cheesekake.api.usecase.UseCaseContext
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.mustBeAuthenticated
import org.yeffrey.cheesekake.domain.skills.CreateSkillGateway
import org.yeffrey.cheesekake.domain.skills.SkillQueryGateway

class CreateSkillImpl(private val skillGateway: CreateSkillGateway, private val queryGateway: SkillQueryGateway) : CreateSkill {
    override fun handle(context: UseCaseContext<CreateSkill.Request>, presenter: UseCasePresenter<SkillDto>) = mustBeAuthenticated(context.principal, presenter) {
        val id = skillGateway.create(context.request.categoryId, context.request.name, context.request.description, it.id)
        val skill = queryGateway.query(id, it.id)
        when (skill) {
            is None -> presenter.notFound()
            is Some -> presenter.success(skill.t.toDto())
        }
    }
}