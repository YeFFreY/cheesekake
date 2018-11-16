package org.yeffrey.cheesekake.domain.activities

import arrow.core.Option
import org.yeffrey.cheesekake.domain.PolicyFun


interface Activity {
    val authorId: Int
    val actions: Map<String, PolicyFun<Activity>>
        get() = mapOf("editable" to ::isAuthor)
}

fun isAuthor(resource: Activity, userId: Option<Int>): Boolean {
    return userId.fold({ false }) { resource.authorId == it }
}
