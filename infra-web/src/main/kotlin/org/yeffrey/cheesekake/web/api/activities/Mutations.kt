package org.yeffrey.cheesekake.web.api.activities

import graphql.schema.idl.TypeRuntimeWiring
import kotlin.random.Random


fun TypeRuntimeWiring.Builder.activityMutations() {
    dataFetcher("createActivity") {
        activities.add(Activity(Random.nextInt(), it.arguments["title"] as String, it.arguments["summary"] as String, null))
        activities.last()
    }
}