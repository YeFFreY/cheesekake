package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDescription
import org.yeffrey.cheesekake.domain.activities.entities.ActivityId
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResources
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary

interface CreateActivityGateway {
    suspend fun create(activityBase: ActivityDescription): ActivityId
}

interface QueryActivityGateway {
    suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary>
    data class ActivityQueryCriteria(val titleContains: Option<String>)
}

interface UpdateActivityGateway {
    suspend fun getDescription(id: ActivityId): Option<ActivityDescription>
    suspend fun updateDescription(activityBase: ActivityDescription): ActivityId
}

interface AddResourcesActivityGateway {
    suspend fun getResources(id: ActivityId): Option<ActivityResources>
    suspend fun updateResources(resources: ActivityResources): ActivityId

}