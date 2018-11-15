package org.yeffrey.cheesekake.domain.activities.query

import org.yeffrey.cheesekake.domain.activities.Activity

data class ActivitySummaryProjection(val id: Int, val title: String, val summary: String, override val authorId: Int) : Activity