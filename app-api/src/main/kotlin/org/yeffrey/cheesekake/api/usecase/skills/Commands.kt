package org.yeffrey.cheesekake.api.usecase.skills

import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.UseCase

interface CreateSkill : UseCase<CreateSkill.Request, SkillDto> {
    data class Request(val categoryId: Int, val name: String, val description: Option<String>)
}