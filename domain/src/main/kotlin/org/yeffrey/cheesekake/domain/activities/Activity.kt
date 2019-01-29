package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.FormattedText

data class ActivityCategory(val id: Int, val name: String, val description: Option<String>)
data class Activity(val id: Int, val title: String, val summary: FormattedText, val category: ActivityCategory)