package org.yeffrey.cheesekake.domain.skills

import arrow.core.Option

interface SkillsQueryGateway {
    fun query(): List<Skill>
}

interface SkillQueryByActivitiesGateway {
    fun query(activityIds: List<Int>): Map<Int, List<Skill>>
}

interface SkillQueryGateway {
    fun query(id: Int, authorId: Int): Option<Skill>
}

interface CreateSkillGateway {
    fun create(categoryId: Int, name: String, description: String, authorId: Int): Int

}