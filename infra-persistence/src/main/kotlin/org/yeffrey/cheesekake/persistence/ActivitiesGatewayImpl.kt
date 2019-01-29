package org.yeffrey.cheesekake.persistence

import arrow.core.Option
import arrow.core.toOption
import org.yeffrey.cheesekake.domain.FormattedText
import org.yeffrey.cheesekake.domain.activities.*
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITIES
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITY_CATEGORIES

class ActivitiesGatewayImpl : ActivitiesQueryGateway, ActivityQueryGateway, CreateActivityGateway, UpdateActivityGeneralInformationGateway {
    override fun query(id: Int, authorId: Int): Option<Activity> = dbQuery {
        val result = it.select(
                ACTIVITIES.ID,
                ACTIVITIES.TITLE,
                ACTIVITIES.SUMMARY_FORMATTED,
                ACTIVITIES.SUMMARY_RAW,
                ACTIVITIES.AUTHOR_ID,
                ACTIVITY_CATEGORIES.ID,
                ACTIVITY_CATEGORIES.NAME,
                ACTIVITY_CATEGORIES.DESCRIPTION)
                .from(ACTIVITIES.innerJoin(ACTIVITY_CATEGORIES).onKey())
                .where(ACTIVITIES.ID.eq(id).and(ACTIVITIES.AUTHOR_ID.eq(authorId)))
                .fetchOne()
        Option.fromNullable(result).map { record ->
            Activity(
                    record[ACTIVITIES.ID], record[ACTIVITIES.TITLE],
                    FormattedText(record[ACTIVITIES.SUMMARY_FORMATTED], record[ACTIVITIES.SUMMARY_RAW]),
                    ActivityCategory(record[ACTIVITY_CATEGORIES.ID], record[ACTIVITY_CATEGORIES.NAME], record[ACTIVITY_CATEGORIES.DESCRIPTION].toOption())
            )
        }
    }

    override fun query(): List<Activity> = dbQuery {
        it.select(
                ACTIVITIES.ID,
                ACTIVITIES.TITLE,
                ACTIVITIES.SUMMARY_FORMATTED,
                ACTIVITIES.SUMMARY_RAW,
                ACTIVITIES.AUTHOR_ID,
                ACTIVITY_CATEGORIES.ID,
                ACTIVITY_CATEGORIES.NAME,
                ACTIVITY_CATEGORIES.DESCRIPTION)
                .from(ACTIVITIES.innerJoin(ACTIVITY_CATEGORIES).onKey())
                .fetch { record ->
                    Activity(
                            record[ACTIVITIES.ID], record[ACTIVITIES.TITLE],
                            FormattedText(record[ACTIVITIES.SUMMARY_FORMATTED], record[ACTIVITIES.SUMMARY_RAW]),
                            ActivityCategory(record[ACTIVITY_CATEGORIES.ID], record[ACTIVITY_CATEGORIES.NAME], record[ACTIVITY_CATEGORIES.DESCRIPTION].toOption())
                    )
                }
    }

    override fun create(categoryId: Int, title: String, summaryFormatted: String, summaryRaw: String, authorId: Int): Int = dbTransaction {
        it.insertInto(ACTIVITIES, ACTIVITIES.CATEGORY_ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY_FORMATTED, ACTIVITIES.SUMMARY_RAW, ACTIVITIES.AUTHOR_ID)
                .values(categoryId, title, summaryFormatted, summaryRaw, authorId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override fun updateGeneralInformation(activityId: Int, categoryId: Int, title: String, summaryFormatted: String, summaryRaw: String, authorId: Int): Int = dbTransaction {
        it.update(ACTIVITIES)
                .set(ACTIVITIES.CATEGORY_ID, categoryId)
                .set(ACTIVITIES.TITLE, title)
                .set(ACTIVITIES.SUMMARY_FORMATTED, summaryFormatted)
                .set(ACTIVITIES.SUMMARY_RAW, summaryRaw)
                .where(ACTIVITIES.ID.eq(activityId).and(ACTIVITIES.AUTHOR_ID.eq(authorId)))
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }


}