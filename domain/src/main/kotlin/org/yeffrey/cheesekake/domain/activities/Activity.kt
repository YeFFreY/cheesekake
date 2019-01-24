package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option

data class ActivityCategory(val id: Int, val name: String, val description: Option<String>)
data class Activity(val id: Int, val title: String, val summary: String, val category: ActivityCategory)