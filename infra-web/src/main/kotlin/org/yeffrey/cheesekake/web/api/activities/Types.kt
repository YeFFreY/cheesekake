package org.yeffrey.cheesekake.web.api.activities

import graphql.schema.idl.TypeRuntimeWiring
import org.dataloader.BatchLoader
import org.yeffrey.cheesekake.api.usecase.UseCasePresenter
import org.yeffrey.cheesekake.api.usecase.activities.ActivityDto
import org.yeffrey.cheesekake.api.usecase.skills.QuerySkillsByActivities
import org.yeffrey.cheesekake.api.usecase.skills.SkillDto
import org.yeffrey.cheesekake.web.WebContext
import org.yeffrey.cheesekake.web.schema.Skill
import org.yeffrey.core.error.ErrorDescription
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun TypeRuntimeWiring.Builder.activityType() {
    dataFetcher("skills") {
        val dataLoader = it.getDataLoader<Int, List<Skill>>("skill")
        dataLoader.load((it.getSource() as ActivityDto).id)
    }
}

class SkillsByActivityId(private val querySkillsByActivities: QuerySkillsByActivities) : BatchLoader<Int, List<SkillDto>> {
    override fun load(keys: MutableList<Int>): CompletionStage<List<List<SkillDto>>> {
        return CompletableFuture.supplyAsync {
            val presenter = ItemByLoaderPresenter()
            querySkillsByActivities.handle(WebContext(QuerySkillsByActivities.Request(keys.toList())), presenter)
            presenter.present()
        }
    }

}

class ItemByLoaderPresenter : UseCasePresenter<Map<Int, List<SkillDto>>> {
    private val viewmodel: MutableList<List<SkillDto>> = mutableListOf()
    override fun success(data: Map<Int, List<SkillDto>>) {
        viewmodel.addAll(data.values.toList())
    }

    override fun fail(errors: List<ErrorDescription>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun present(): List<List<SkillDto>> {
        return viewmodel.toList()
    }

    override fun notFound() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
