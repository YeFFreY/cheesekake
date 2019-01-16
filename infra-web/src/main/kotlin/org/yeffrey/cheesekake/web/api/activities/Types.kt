package org.yeffrey.cheesekake.web.api.activities

import graphql.schema.idl.TypeRuntimeWiring
import org.dataloader.BatchLoader
import org.yeffrey.cheesekake.web.api.skills.skills
import org.yeffrey.cheesekake.web.schema.Activity
import org.yeffrey.cheesekake.web.schema.Skill
import java.util.concurrent.CompletableFuture

fun TypeRuntimeWiring.Builder.activityType() {
    dataFetcher("skills") {
        val dataLoader = it.getDataLoader<Int, List<Skill>>("skill")
        dataLoader.load((it.getSource() as Activity).id)
    }
}

fun skillsByActivityLoader(): BatchLoader<Int, List<Skill>> {
    return BatchLoader {
        println(it)
        CompletableFuture.supplyAsync {
            val res = mutableListOf<List<Skill>?>()
            (0..(it.size - 1)).forEach {
                res.add(skills)
            }
            res.toList()
        }
    }
}
