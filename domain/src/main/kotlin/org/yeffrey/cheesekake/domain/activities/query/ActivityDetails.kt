package org.yeffrey.cheesekake.domain.activities.query

data class ActivityDetails(val id: Int, val title: String, val summary: String, val resources: List<Int> = emptyList(), val skills: List<Int> = emptyList())