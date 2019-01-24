package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option

interface ActivitiesQueryGateway {
    fun query(): List<Activity>
}

interface ActivityQueryGateway {
    fun query(id: Int, authorId: Int): Option<Activity>
}

interface CreateActivityGateway {
    fun create(categoryId: Int, title: String, summary: Option<String>, authorId: Int): Int
}