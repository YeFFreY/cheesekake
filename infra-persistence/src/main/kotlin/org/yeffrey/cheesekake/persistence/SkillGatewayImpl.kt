package org.yeffrey.cheesekake.persistence

import org.yeffrey.cheesekake.domain.skills.Skill
import org.yeffrey.cheesekake.domain.skills.SkillQueryByActivitiesGateway
import org.yeffrey.cheesekake.domain.skills.SkillQueryGateway
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITY_SKILLS
import org.yeffrey.cheesekake.persistence.db.Tables.SKILLS

class SkillGatewayImpl : SkillQueryGateway, SkillQueryByActivitiesGateway {
    override fun query(activityIds: List<Int>): Map<Int, List<Skill>> = dbQuery { dslContext ->
        val result: Map<Int, MutableList<Skill>> = activityIds.map { it to mutableListOf<Skill>() }.toMap()

        dslContext.select(SKILLS.ID, SKILLS.NAME, SKILLS.DESCRIPTION, ACTIVITY_SKILLS.ACTIVITY_ID)
                .from(SKILLS.innerJoin(ACTIVITY_SKILLS).onKey())
                .where(ACTIVITY_SKILLS.ACTIVITY_ID.`in`(activityIds))
                .fetch { record ->
                    result[record[ACTIVITY_SKILLS.ACTIVITY_ID]]?.add(Skill(record[SKILLS.ID], record[SKILLS.NAME], record[SKILLS.DESCRIPTION]))
                }
        result
    }

    override fun query(): List<Skill> = dbQuery {
        it.select(SKILLS.ID, SKILLS.NAME, SKILLS.DESCRIPTION)
                .from(SKILLS)
                .fetch { record ->
                    Skill(record[SKILLS.ID], record[SKILLS.NAME], record[SKILLS.DESCRIPTION])
                }
    }
}