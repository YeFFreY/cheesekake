package org.yeffrey.cheesekake.domain.activities

interface ActivityQueryGateway {
    suspend fun query(): List<Activity>
}