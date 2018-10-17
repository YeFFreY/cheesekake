package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.command.NewActivity
import org.yeffrey.cheesekake.domain.activities.command.UpdatedActivity
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary

interface CreateActivityGateway {
    suspend fun create(activity: NewActivity): Int
}

interface QueryActivityGateway {
    suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary>
}

interface UpdateActivitySummaryGateway {
    suspend fun authorOf(activityId: Int): Int
    suspend fun update(activity: UpdatedActivity): Int
}
data class ActivityQueryCriteria(val titleContains: Option<String>)