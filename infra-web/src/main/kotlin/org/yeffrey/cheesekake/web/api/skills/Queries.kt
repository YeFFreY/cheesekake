package org.yeffrey.cheesekake.web.api.skills

import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.web.schema.Skill


val skills = listOf(
        Skill(1, "sKill1"),
        Skill(2, "sKill2")
)

fun TypeRuntimeWiring.Builder.skillQueries() {
    dataFetcher("skills") {
        skills
    }
}
