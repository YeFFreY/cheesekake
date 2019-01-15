package org.yeffrey.cheesekake.web.schema

data class Skill(val id: Int, val name: String)
data class MinMax(val min: Int, val max: Int)
data class ActivityMetadata(val duration: MinMax?, val participants: MinMax?, val age: MinMax?)
data class Activity(val id: Int, val title: String, val summary: String, val meta: ActivityMetadata?, val skills: List<Skill> = emptyList())