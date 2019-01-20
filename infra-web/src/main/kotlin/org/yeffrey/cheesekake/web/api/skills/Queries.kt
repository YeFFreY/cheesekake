package org.yeffrey.cheesekake.web.api.skills

import graphql.schema.idl.TypeRuntimeWiring
import org.yeffrey.cheesekake.api.usecase.skills.QueryMySkills
import org.yeffrey.cheesekake.api.usecase.skills.SkillDto
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.fetcherPresenter

fun TypeRuntimeWiring.Builder.skillQueries(queryMySkills: QueryMySkills) {
    fetcherPresenter<List<SkillDto>>("skills") { _, presenter ->
        queryMySkills.handle(WebContext(QueryMySkills.Request()), presenter)
    }
}
