package org.yeffrey.cheesekake.api.usecase.skills

import org.yeffrey.cheesekake.api.usecase.UseCase

data class SkillDto(val id: Int, val name: String, val description: String)
interface QueryMySkills : UseCase<QueryMySkills.Request, List<SkillDto>> {
    data class Request(val filter: String = "")
}

interface QuerySkillsByActivities : UseCase<QuerySkillsByActivities.Request, Map<Int, List<SkillDto>>> {
    data class Request(val activityIds: List<Int> = emptyList())
}