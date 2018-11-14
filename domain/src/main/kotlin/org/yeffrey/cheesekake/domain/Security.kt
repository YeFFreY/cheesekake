package org.yeffrey.cheesekake.domain


interface Policy<R> {
    val name: String
    fun enforce(resource: R, userId: Int): Boolean
}


fun <Y> available(policies: List<Policy<Y>>, userId: Int, resource: Y): List<Policy<Y>> {
    return policies.mapNotNull {
        if (it.enforce(resource, userId)) it else null
    }
}

fun <Y> nameOf(policies: List<Policy<Y>>): List<String> = policies.map(Policy<Y>::name)

fun <Y> respect(userId: Int, resource: Y, vararg policies: Policy<Y>): Boolean = respect(userId, resource, policies.toList())

fun <Y> respect(userId: Int, resource: Y, policies: List<Policy<Y>>): Boolean {
    for (policy in policies) {
        if (!policy.enforce(resource, userId)) {
            return false
        }
    }
    return true
}

/*

fun main(args: Array<String>) {
    val policies: List<Policy<Act>> = ActPolicy.values().asList()

    val act = Act(5)
    println(nameOf(available(policies, 5, act)))
    println(respect(policies, 1, act))


}*/
