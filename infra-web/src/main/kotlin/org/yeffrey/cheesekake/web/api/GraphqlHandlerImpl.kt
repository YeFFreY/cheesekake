package org.yeffrey.cheesekake.web.api

import graphql.ExecutionInput.newExecutionInput
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.yeffrey.cheesekake.api.usecase.activities.CreateActivity
import org.yeffrey.cheesekake.api.usecase.activities.QueryActivity
import org.yeffrey.cheesekake.api.usecase.activities.QueryMyActivities
import org.yeffrey.cheesekake.api.usecase.activities.UpdateActivityGeneralInformation
import org.yeffrey.cheesekake.api.usecase.skills.CreateSkill
import org.yeffrey.cheesekake.api.usecase.skills.QueryMySkills
import org.yeffrey.cheesekake.api.usecase.skills.QuerySkillsByActivities
import org.yeffrey.cheesekake.web.GraphqlHandler
import org.yeffrey.cheesekake.web.GraphqlRequest
import org.yeffrey.cheesekake.web.api.activities.SkillsByActivityId
import org.yeffrey.cheesekake.web.api.activities.activityMutations
import org.yeffrey.cheesekake.web.api.activities.activityQueries
import org.yeffrey.cheesekake.web.api.activities.activityType
import org.yeffrey.cheesekake.web.api.skills.skillMutations
import org.yeffrey.cheesekake.web.api.skills.skillQueries
import org.yeffrey.cheesekake.web.core.filter.Session
import org.yeffrey.cheesekake.web.routes
import java.io.File

class GraphqlHandlerImpl(
        queryMyActivities: QueryMyActivities,
        queryActivity: QueryActivity,
        createActivity: CreateActivity,
        updateActivityGeneralInformation: UpdateActivityGeneralInformation,
        createSkill: CreateSkill,
        queryMySkills: QueryMySkills,
        querySkillsByActivities: QuerySkillsByActivities
) : GraphqlHandler {
    private var graphql: GraphQL
    private var skillsByActivityId: SkillsByActivityId

    init {
        val schemaParser = SchemaParser()
        val typeDefinitionRegistry = schemaParser.parse(File(ClassLoader.getSystemResource("schema.graphqls").file))
        val runtimeWiring = newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .routes {
                            activityQueries(queryMyActivities, queryActivity)
                            skillQueries(queryMySkills)
                        }
                )
                .type(TypeRuntimeWiring.newTypeWiring("Activity")
                        .routes {
                            activityType()
                        }
                )
                .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                        .routes {
                            activityMutations(createActivity, updateActivityGeneralInformation)
                            skillMutations(createSkill)
                        }
                )
                .build()
        val schemaGenerator = SchemaGenerator()
        val graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
        graphql = GraphQL.newGraphQL(graphQLSchema).build()
        skillsByActivityId = SkillsByActivityId(querySkillsByActivities)
    }

    override fun invoke(request: GraphqlRequest, session: Session): MutableMap<String, Any> {
        val registry = DataLoaderRegistry()
        registry.register("skill", DataLoader.newDataLoader(skillsByActivityId))

        val executionInput = newExecutionInput()
                .context(session)
                .query(request.query)
                .dataLoaderRegistry(registry)
                .operationName(request.operationName)
                .variables(request.variables)
        val executionResult = graphql.execute(executionInput.build())
        return executionResult.toSpecification()
    }
}