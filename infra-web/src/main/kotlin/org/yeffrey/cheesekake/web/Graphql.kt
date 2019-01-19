package org.yeffrey.cheesekake.web

import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.core.error.ErrorDescription

data class GraphqlRequest(var query: String?, var operationName: String?, var variables: Map<String, Any>?)
class GraphqlPresenter : UseCasePresenter {
    private lateinit var viewModel: Any

    override fun fail(errors: List<ErrorDescription>) {
        viewModel = errors.stream().map(ErrorDescription::message)
    }

    override fun success(data: Any) {
        viewModel = data;
    }

    fun present(): Any {
        return viewModel
    }

}

fun TypeRuntimeWiring.Builder.routes(build: TypeRuntimeWiring.Builder.() -> Unit) = apply(build)
