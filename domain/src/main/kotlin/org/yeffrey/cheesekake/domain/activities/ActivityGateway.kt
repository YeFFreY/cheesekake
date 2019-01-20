package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option

interface ActivitiesQueryGateway {
    fun query(): List<Activity>
}

interface ActivityQueryGateway {
    fun query(id: Int): Option<Activity>
}