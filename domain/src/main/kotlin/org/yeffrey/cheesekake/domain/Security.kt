package org.yeffrey.cheesekake.domain

import arrow.core.Option
import arrow.core.toOption


typealias PolicyFun<R> = (resource: R, userId: Option<Int>) -> Boolean

fun <Y> respect(userId: Option<Int>, resource: Y, vararg policies: PolicyFun<Y>): Boolean = respect(userId, resource, policies.toList())
fun <Y> respect(userId: Int, resource: Y, vararg policies: PolicyFun<Y>): Boolean = respect(userId.toOption(), resource, policies.toList())

fun <Y> respect(userId: Option<Int>, resource: Y, policies: List<PolicyFun<Y>>): Boolean {
    for (policy in policies) {
        if (!policy(resource, userId)) {
            return false
        }
    }
    return true
}




