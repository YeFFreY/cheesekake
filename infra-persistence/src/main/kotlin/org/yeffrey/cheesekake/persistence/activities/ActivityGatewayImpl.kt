package org.yeffrey.cheesekake.persistence.activities

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toOption
import arrow.data.getOrElse
import org.jooq.Condition
import org.jooq.impl.DSL
import org.yeffrey.cheesekake.domain.activities.ActivityQueryCriteria
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.*
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITIES

class ActivityGatewayImpl : CreateActivityGateway, UpdateActivityGateway, QueryActivityGateway {
    override suspend fun get(id: ActivityId): Option<ActivityBase> = dbQuery {
        val record = it.fetchOne(ACTIVITIES, ACTIVITIES.ID.eq(id))
        Option.fromNullable(record).map { activity ->
            ActivityBase(activity[ACTIVITIES.ID].toOption(), activity[ACTIVITIES.TITLE].activityTitle().getOrElse { ActivityTitle.invalid(activity[ACTIVITIES.TITLE]) }, activity[ACTIVITIES.SUMMARY], Writer(1))
        }
    }

    override suspend fun update(activityBase: ActivityBase): ActivityId = dbQuery {
        it.update(ACTIVITIES)
                .set(ACTIVITIES.TITLE, activityBase.title.value)
                .set(ACTIVITIES.SUMMARY, activityBase.summary)
                .where(ACTIVITIES.ID.eq(activityBase.id.orNull()))
                .returning(ACTIVITIES.ID)
                .fetchOne().getValue(ACTIVITIES.ID)
    }

    override suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary> = dbQuery {
        it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(query.toCondition())
                .fetch { record ->
                    ActivitySummary(record[ACTIVITIES.ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
                }
    }

    private fun ActivityQueryCriteria.toCondition(): Condition {
        var condition: Condition = DSL.trueCondition()
        val titleContains = this.titleContains
        condition = when (titleContains) {
            is Some -> condition.and(ACTIVITIES.TITLE.containsIgnoreCase(titleContains.t))
            is None -> condition
        }
        return condition
    }

    override suspend fun create(activityBase: ActivityBase): Int = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .values(activityBase.title.value, activityBase.summary, activityBase.writer.userId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }
}