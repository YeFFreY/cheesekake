package org.yeffrey.cheesekake.web

import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.core.error.ErrorDescription

data class GraphqlRequest(var query: String?, var operationName: String?, var variables: Map<String, Any>?)
class GraphqlPresenter : UseCasePresenter {
    lateinit var viewModel: Any

    override suspend fun fail(errors: List<ErrorDescription>) {
        viewModel = errors.stream().map(ErrorDescription::message)
    }

    override suspend fun success(data: Any) {
        viewModel = data;
    }

    fun present(): Any {
        return viewModel
    }

}

fun TypeRuntimeWiring.Builder.route(build: TypeRuntimeWiring.Builder.() -> Unit) = apply(build)
