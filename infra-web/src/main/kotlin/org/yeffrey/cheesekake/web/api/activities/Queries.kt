package org.yeffrey.cheesekake.web.api.activities

import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivities
import org.yeffrey.cheesekake.web.GraphqlPresenter
import org.yeffrey.cheesekake.web.schema.Activity
import org.yeffrey.cheesekake.web.schema.ActivityMetadata
import org.yeffrey.cheesekake.web.schema.MinMax


val activities = mutableListOf(
        Activity(1, "bob", "theBob", ActivityMetadata(MinMax(5, 20), MinMax(3, 7), MinMax(3, 5))),
        Activity(2, "bob 2", "theBob 2", null),
        Activity(3, "bob 3", "theBob 3", ActivityMetadata(MinMax(20, 50), null, MinMax(5, 10)))
)


fun kakeFetcher(dfe: DataFetchingEnvironment) = () -> Any {
    val presenter = GraphqlPresenter()
    //block(presenter)
    presenter.present()
}

fun TypeRuntimeWiring.Builder.activityQueries(queryMyActivities: QueryMyActivities) {

    dataFetcher("activities") {
        //queryMyActivities.handle(WebContext(QueryMyActivities.Request()), it)
    }
    dataFetcher("activity") {
        val id: Int = (it.arguments["id"] as String).toInt()
        activities.stream().filter { it.id == id }.findFirst()
    }
    }
}