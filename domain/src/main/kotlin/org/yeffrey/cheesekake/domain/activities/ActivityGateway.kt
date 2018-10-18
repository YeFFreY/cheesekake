package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.entities.Activity
import org.yeffrey.cheesekake.domain.activities.entities.ActivityId
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary

interface CreateActivityGateway {
    suspend fun create(activity: Activity): ActivityId
}

interface QueryActivityGateway {
    suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary>
}

interface UpdateActivitySummaryGateway {
    suspend fun update(activity: Activity): ActivityId
}
data class ActivityQueryCriteria(val titleContains: Option<String>)