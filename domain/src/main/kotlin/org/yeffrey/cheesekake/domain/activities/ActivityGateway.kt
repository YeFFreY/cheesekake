package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.entities.*
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary

interface CreateActivityGateway {
    suspend fun nextIdentity(): ActivityId
    suspend fun activityCreated(data: ActivityCreated): ActivityId
}

interface QueryActivityGateway {
    suspend fun query(query: ActivityQueryCriteria): List<ActivitySummary>
    data class ActivityQueryCriteria(val titleContains: Option<String>)
}

interface UpdateActivityGateway {
    suspend fun getDescription(id: ActivityId): Option<Activity>
    suspend fun descriptionUpdated(data: ActivityDescriptionUpdated): ActivityId
}

interface AddResourcesActivityGateway {
    suspend fun getResources(id: ActivityId): Option<Activity>
    suspend fun exists(id: ResourceId): Boolean
    suspend fun resourceAdded(data: ActivityResourceAdded): ActivityId

}