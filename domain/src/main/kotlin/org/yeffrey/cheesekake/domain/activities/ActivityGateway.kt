package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.entities.ActivityBase
import org.yeffrey.cheesekake.domain.activities.entities.ActivityId
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary

interface CreateActivityGateway {
    suspend fun create(activityBase: ActivityBase): ActivityId
}

interface QueryActivityGateway {
    suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary>
}

interface UpdateActivityGateway {
    suspend fun get(id: ActivityId) : Option<ActivityBase>
    suspend fun update(activityBase: ActivityBase): ActivityId
}
data class ActivityQueryCriteria(val titleContains: Option<String>)