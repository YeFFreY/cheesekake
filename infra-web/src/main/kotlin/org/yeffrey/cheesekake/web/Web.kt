package org.yeffrey.cheesekake.web

import arrow.core.Option
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.yeffrey.cheesekake.api.usecase.Principal
import org.yeffrey.cheesekake.api.usecase.UseCaseContext

class WebContext<R>(override val request: R, override val principal: Option<Principal> = Option.empty()) : UseCaseContext<R>
object JsonMapper {
    private val mapper = jacksonObjectMapper()

    fun <T> mapTo(arguments: Map<String, Any>, toValueType: Class<T>): T = mapper.convertValue(arguments, toValueType)
}