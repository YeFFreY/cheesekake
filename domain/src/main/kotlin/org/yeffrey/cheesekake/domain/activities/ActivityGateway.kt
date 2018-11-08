package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.activities.entities.ActivityCreated
import org.yeffrey.cheesekake.domain.activities.entities.ActivityDetailsCorrected
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResourceAdded
import org.yeffrey.cheesekake.domain.activities.entities.ActivityResourcesRequirement
import org.yeffrey.cheesekake.domain.activities.query.ActivityDetailsProjection
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummaryProjection

interface CreateActivityGateway {
    suspend fun nextIdentity(): Int
    suspend fun activityCreated(data: ActivityCreated): Int
}

interface QueryActivitiesGateway {
    suspend fun query(query: ActivityQueryCriteria): List<ActivitySummaryProjection>
    data class ActivityQueryCriteria(val titleContains: Option<String>)
}

interface QueryActivityGateway {
    suspend fun get(id: Int): Option<ActivityDetailsProjection>
}

interface UpdateActivityGateway {
    suspend fun getDescription(id: Int): Option<org.yeffrey.cheesekake.domain.activities.entities.ActivityDetails>
    suspend fun descriptionUpdated(data: ActivityDetailsCorrected): Int
}

interface AddResourcesActivityGateway {
    suspend fun getResources(id: Int): Option<ActivityResourcesRequirement>
    suspend fun exists(id: Int): Boolean
    suspend fun resourceAdded(data: ActivityResourceAdded): Int

}