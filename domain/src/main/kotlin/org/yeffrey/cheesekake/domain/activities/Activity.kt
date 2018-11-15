package org.yeffrey.cheesekake.domain.activities

import org.yeffrey.cheesekake.domain.PolicyFun


interface Activity {
    val authorId: Int
    val actions: Map<String, PolicyFun<Activity>>
        get() = mapOf("editable" to ::isAuthor)
}

fun isAuthor(resource: Activity, userId: Int): Boolean {
    return resource.authorId == userId
}
