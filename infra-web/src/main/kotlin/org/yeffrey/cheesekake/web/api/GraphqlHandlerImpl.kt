package org.yeffrey.cheesekake.web.api

import graphql.ExecutionInput.newExecutionInput
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.yeffrey.cheesekake.web.GraphqlHandler
import org.yeffrey.cheesekake.web.GraphqlRequest

class GraphqlHandlerImpl(val request: GraphqlRequest) : GraphqlHandler {
    override fun invoke(request: GraphqlRequest) {
        val skillDataLoader = DataLoader.newDataLoader(skillsByActivityLoader)
        val registry = DataLoaderRegistry()
        registry.register("skill", skillDataLoader)
        val executionInput = newExecutionInput()
                .query(request.query)
                .dataLoaderRegistry(registry)
                .operationName(request.operationName)
                .variables(request.variables)
        val executionResult = graphql.execute(executionInput.build())
    }
}