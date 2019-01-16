package org.yeffrey.cheesekake.web.api.activities

import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.TypeRuntimeWiring
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivities
import org.yeffrey.cheesekake.web.GraphqlPresenter
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.schema.Activity
import org.yeffrey.cheesekake.web.schema.ActivityMetadata
import org.yeffrey.cheesekake.web.schema.MinMax
import java.util.concurrent.CompletableFuture


val activities = mutableListOf(
        Activity(1, "bob", "theBob", ActivityMetadata(MinMax(5, 20), MinMax(3, 7), MinMax(3, 5))),
        Activity(2, "bob 2", "theBob 2", null),
        Activity(3, "bob 3", "theBob 3", ActivityMetadata(MinMax(20, 50), null, MinMax(5, 10)))
)

fun bob(dfe: DataFetchingEnvironment, block: suspend (presenter: GraphqlPresenter) -> Any): CompletableFuture<Any> {
    return GlobalScope.async {
        val presenter = GraphqlPresenter()
        block(presenter)
        presenter.present()
    }.asCompletableFuture()
}

fun TypeRuntimeWiring.Builder.activityQueries(queryMyActivities: QueryMyActivities) {
    dataFetcher("activities") {
        bob(it) {
            queryMyActivities.handle(context = WebContext(QueryMyActivities.Request()), presenter = it)
        }
    }
    dataFetcher("activity") {
        val id: Int = (it.arguments["id"] as String).toInt()
        activities.stream().filter { it.id == id }.findFirst()
    }
}