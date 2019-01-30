package org.yeffrey.cheesekake.persistence

import arrow.core.toOption
import org.yeffrey.cheesekake.domain.activities.ActivityCategoriesQueryGateway
import org.yeffrey.cheesekake.domain.activities.ActivityCategory
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITY_CATEGORIES

class ActivityCategoriesGatewayImpl : ActivityCategoriesQueryGateway {
    override fun query(): List<ActivityCategory> = DatabaseManager.dbQuery {
        it.select(
                ACTIVITY_CATEGORIES.ID,
                ACTIVITY_CATEGORIES.NAME,
                ACTIVITY_CATEGORIES.DESCRIPTION)
                .from(ACTIVITY_CATEGORIES)
                .fetch { record ->
                    ActivityCategory(
                            record[ACTIVITY_CATEGORIES.ID], record[ACTIVITY_CATEGORIES.NAME], record[ACTIVITY_CATEGORIES.DESCRIPTION].toOption()
                    )
                }
    }
}