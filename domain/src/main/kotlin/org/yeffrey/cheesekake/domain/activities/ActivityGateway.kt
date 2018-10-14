package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.command.NewActivity
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary

interface CreateActivityGateway {
    suspend fun create(activity: NewActivity): Int
}

interface QueryActivityGateway {
    suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary>
}
data class ActivityQueryCriteria(val titleContains: Option<String>)