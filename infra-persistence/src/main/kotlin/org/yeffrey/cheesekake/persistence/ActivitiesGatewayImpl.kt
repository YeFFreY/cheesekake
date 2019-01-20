package org.yeffrey.cheesekake.persistence

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.ActivitiesQueryGateway
import org.yeffrey.cheesekake.domain.activities.Activity
import org.yeffrey.cheesekake.domain.activities.ActivityQueryGateway
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITIES

class ActivitiesGatewayImpl : ActivitiesQueryGateway, ActivityQueryGateway {
    override fun query(id: Int): Option<Activity> = dbQuery {
        val result = it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(result).map { record ->
            Activity(record[ACTIVITIES.ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
        }
    }

    override fun query(): List<Activity> = dbQuery {
        it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .from(ACTIVITIES)
                .fetch { record ->
                    Activity(record[ACTIVITIES.ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
                }
    }
}