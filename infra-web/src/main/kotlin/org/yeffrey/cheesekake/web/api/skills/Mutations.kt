package org.yeffrey.cheesekake.web.api.skills

import arrow.core.Option
import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.skills.CreateSkill
import org.yeffrey.cheesekake.api.usecase.skills.SkillDto
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.core.filter.Session
import org.yeffrey.cheesekake.web.fetcherPresenter

private fun from(arguments: Map<String, Any>): CreateSkill.Request {
    val categoryId = arguments["categoryId"] as Int
    val title = arguments["name"] as String
    val description = Option.fromNullable(arguments["description"] as? String)
    return CreateSkill.Request(categoryId, title, description)
}

fun TypeRuntimeWiring.Builder.skillMutations(createSkill: CreateSkill) {
    fetcherPresenter<SkillDto>("createSkill") { dfe, presenter ->
        val principal = dfe.getContext<Session>().principal
        createSkill.handle(WebContext(from(dfe.arguments), principal), presenter)
    }
}