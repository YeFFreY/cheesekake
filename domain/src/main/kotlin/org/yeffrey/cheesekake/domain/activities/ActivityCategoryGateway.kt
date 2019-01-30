package org.yeffrey.cheesekake.domain.activities

interface ActivityCategoriesQueryGateway {
    fun query(): List<ActivityCategory>
}