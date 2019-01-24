package org.yeffrey.cheesekake.persistence

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.ActivitiesQueryGateway
import org.yeffrey.cheesekake.domain.activities.Activity
import org.yeffrey.cheesekake.domain.activities.ActivityQueryGateway
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITIES

class ActivitiesGatewayImpl : ActivitiesQueryGateway, ActivityQueryGateway, CreateActivityGateway {
    override fun query(id: Int, authorId: Int): Option<Activity> = dbQuery {
        val result = it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id).and(ACTIVITIES.AUTHOR_ID.eq(authorId)))
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

    override fun create(categoryId: Int, title: String, summary: Option<String>, authorId: Int): Int = dbTransaction {
        it.insertInto(ACTIVITIES, ACTIVITIES.CATEGORY_ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .values(categoryId, title, summary.orNull(), authorId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }
}