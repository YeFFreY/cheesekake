package org.yeffrey.cheesekake.domain.skills

interface SkillQueryGateway {
    fun query(): List<Skill>
}

interface SkillQueryByActivitiesGateway {
    fun query(activityIds: List<Int>): Map<Int, List<Skill>>
}