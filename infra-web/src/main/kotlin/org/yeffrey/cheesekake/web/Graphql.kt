package org.yeffrey.cheesekake.web

import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.core.error.ErrorDescription

data class GraphqlRequest(var query: String?, var operationName: String?, var variables: Map<String, Any>?)

class GraphqlPresenter<D> : UseCasePresenter<D> {
    override fun accessDenied() {
        throw AccessDeniedError()
    }

    override fun notFound() {
        viewModel = null
    }

    private var viewModel: Any? = null

    override fun fail(errors: List<ErrorDescription>) {
        throw ValidationError(errors)
    }

    override fun success(data: D) {
        viewModel = data!!
    }

    fun present(): Any? {
        return viewModel
    }

}

fun TypeRuntimeWiring.Builder.routes(build: TypeRuntimeWiring.Builder.() -> Unit) = apply(build)

fun <D> TypeRuntimeWiring.Builder.fetcherPresenter(fieldName: String, block: (dfe: DataFetchingEnvironment, presenter: GraphqlPresenter<D>) -> Any) {
    dataFetcher(fieldName) {
        val presenter = GraphqlPresenter<D>()
        block(it, presenter)
        presenter.present()
    }
}