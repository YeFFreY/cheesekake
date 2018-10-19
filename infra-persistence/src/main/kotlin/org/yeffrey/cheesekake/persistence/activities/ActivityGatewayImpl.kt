package org.yeffrey.cheesekake.persistence.activities

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.data.getOrElse
import org.jooq.Condition
import org.jooq.impl.DSL
import org.yeffrey.cheesekake.domain.activities.ActivityQueryCriteria
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.ActivityId
import org.yeffrey.cheesekake.domain.activities.entities.ActivityTitle
import org.yeffrey.cheesekake.domain.activities.entities.activityTitle
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITIES

class ActivityGatewayImpl : CreateActivityGateway, UpdateActivityGateway, QueryActivityGateway {
    override suspend fun get(id: ActivityId): Option<Activity> = dbQuery {
        val record = it.fetchOne(ACTIVITIES, ACTIVITIES.ID.eq(id))
        Option.fromNullable(record).map { activity ->
            Activity.from(activity[ACTIVITIES.ID], activity[ACTIVITIES.TITLE].activityTitle().getOrElse { ActivityTitle.invalid(activity[ACTIVITIES.TITLE]) }, activity[ACTIVITIES.SUMMARY], 1)
        }
    }

    override suspend fun update(activity: Activity): ActivityId = dbQuery {
        it.update(ACTIVITIES)
                .set(ACTIVITIES.TITLE, activity.title.value)
                .set(ACTIVITIES.SUMMARY, activity.summary)
                .where(ACTIVITIES.ID.eq(activity.id.orNull()))
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

    override suspend fun create(activity: Activity): Int = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .values(activity.title.value, activity.summary, activity.authorId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }
}