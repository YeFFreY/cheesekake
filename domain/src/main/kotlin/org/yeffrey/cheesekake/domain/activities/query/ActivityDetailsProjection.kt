package org.yeffrey.cheesekake.domain.activities.query

data class ActivityResourceProjection(val resourceId: Int, val quantity: Int)
data class ActivityDetailsProjection(val id: Int, val title: String, val summary: String, val resources: List<ActivityResourceProjection> = emptyList(), val skills: List<Int> = emptyList())