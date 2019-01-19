package org.yeffrey.cheesekake.domain.activities

interface ActivityQueryGateway {
    fun query(): List<Activity>
}