package org.yeffrey.cheesekake.web

import graphql.schema.idl.TypeRuntimeWiring

data class GraphqlRequest(var query: String?, var operationName: String?, var variables: Map<String, Any>?)

fun TypeRuntimeWiring.Builder.route(build: TypeRuntimeWiring.Builder.() -> Unit): TypeRuntimeWiring.Builder = apply(build)