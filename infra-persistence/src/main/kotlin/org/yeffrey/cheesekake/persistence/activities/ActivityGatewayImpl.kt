package org.yeffrey.cheesekake.persistence.activities

import arrow.core.None
import arrow.core.Some
import org.jooq.Condition
import org.jooq.impl.DSL
import org.yeffrey.cheesekake.domain.activities.ActivityQueryCriteria
import org.yeffrey.cheesekake.domain.activities.command.NewActivity
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITIES

class ActivityGatewayImpl : CreateActivityGateway, QueryActivityGateway {
    override suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary> = dbQuery {
        it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(query.toCondition())
                .fetch { record ->
                    ActivitySummary(record[ACTIVITIES.ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
                }
    }

    private fun ActivityQueryCriteria.toCondition(): Condition {
        var condition : Condition = DSL.trueCondition()
        val titleContains = this.titleContains
        condition = when (titleContains) {
            is Some -> condition.and(ACTIVITIES.TITLE.containsIgnoreCase(titleContains.t))
            is None -> condition
        }
        return condition
    }

    override suspend fun create(activity: NewActivity): Int = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .values(activity.title.value, activity.summary)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }
}