package org.yeffrey.cheesekake.domain


interface Policy<R> {
    val name: String
    fun enforce(resource: R, userId: Int): Boolean
}

typealias PolicyFun<R> = (resource: R, userId: Int) -> Boolean

fun <Y> available(policies: List<PolicyFun<Y>>, userId: Int, resource: Y): List<PolicyFun<Y>> {
    return policies.mapNotNull {
        if (it(resource, userId)) it else null
    }
}

fun <Y> respect(userId: Int, resource: Y, vararg policies: PolicyFun<Y>): Boolean = respect(userId, resource, policies.toList())

fun <Y> respect(userId: Int, resource: Y, policies: List<PolicyFun<Y>>): Boolean {
    for (policy in policies) {
        if (!policy(resource, userId)) {
            return false
        }
    }
    return true
}




