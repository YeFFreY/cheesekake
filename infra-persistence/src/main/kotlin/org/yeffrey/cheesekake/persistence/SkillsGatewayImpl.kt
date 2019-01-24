package org.yeffrey.cheesekake.persistence

import arrow.core.Option
import arrow.core.toOption
import org.yeffrey.cheesekake.domain.skills.*
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITY_SKILLS
import org.yeffrey.cheesekake.persistence.db.Tables.SKILLS

class SkillsGatewayImpl : SkillsQueryGateway, SkillQueryByActivitiesGateway, SkillQueryGateway, CreateSkillGateway {
    override fun create(categoryId: Int, name: String, description: Option<String>, authorId: Int): Int = dbTransaction {
        it.insertInto(SKILLS, SKILLS.CATEGORY_ID, SKILLS.NAME, SKILLS.DESCRIPTION, SKILLS.AUTHOR_ID)
                .values(categoryId, name, description.orNull(), authorId)
                .returning(SKILLS.ID)
                .fetchOne()[SKILLS.ID]
    }

    override fun query(id: Int, authorId: Int): Option<Skill> = dbQuery { dslContext ->
        val result = dslContext.select(SKILLS.ID, SKILLS.NAME, SKILLS.DESCRIPTION, SKILLS.AUTHOR_ID)
                .from(SKILLS)
                .where(SKILLS.ID.eq(id).and(SKILLS.AUTHOR_ID.eq(authorId)))
                .fetchOne()
        Option.fromNullable(result).map { record ->
            Skill(record[SKILLS.ID], record[SKILLS.NAME], record[SKILLS.DESCRIPTION].toOption())
        }
    }

    override fun query(activityIds: List<Int>): Map<Int, List<Skill>> = dbQuery { dslContext ->
        val result: Map<Int, MutableList<Skill>> = activityIds.map { it to mutableListOf<Skill>() }.toMap()

        dslContext.select(SKILLS.ID, SKILLS.NAME, SKILLS.DESCRIPTION, ACTIVITY_SKILLS.ACTIVITY_ID)
                .from(SKILLS.innerJoin(ACTIVITY_SKILLS).onKey())
                .where(ACTIVITY_SKILLS.ACTIVITY_ID.`in`(activityIds))
                .fetch { record ->
                    result[record[ACTIVITY_SKILLS.ACTIVITY_ID]]?.add(Skill(record[SKILLS.ID], record[SKILLS.NAME], record[SKILLS.DESCRIPTION].toOption()))
                }
        result
    }

    override fun query(): List<Skill> = dbQuery {
        it.select(SKILLS.ID, SKILLS.NAME, SKILLS.DESCRIPTION)
                .from(SKILLS)
                .fetch { record ->
                    Skill(record[SKILLS.ID], record[SKILLS.NAME], record[SKILLS.DESCRIPTION].toOption())
                }
    }
}