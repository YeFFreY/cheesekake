package org.yeffrey.cheesekake.persistence.activities

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.jooq.Condition
import org.jooq.impl.DSL
import org.yeffrey.cheesekake.domain.activities.*
import org.yeffrey.cheesekake.domain.activities.entities.*
import org.yeffrey.cheesekake.domain.activities.query.ActivityDetailsProjection
import org.yeffrey.cheesekake.domain.activities.query.ActivityResourceProjection
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummaryProjection
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Sequences.ACTIVITIES_ID_SEQ
import org.yeffrey.cheesekake.persistence.db.Tables.*

class ActivityGatewayImpl : CreateActivityGateway, UpdateActivityGateway, QueryActivitiesGateway, QueryActivityGateway, AddResourcesActivityGateway {
    override suspend fun get(id: Int): Option<ActivityDetailsProjection> = dbQuery {
        val record = it.select(ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(record).map { activity ->
            val resources = it.select(ACTIVITY_RESOURCES.RESOURCE_ID, ACTIVITY_RESOURCES.QUANTITY).from(ACTIVITY_RESOURCES).where(ACTIVITY_RESOURCES.ACTIVITY_ID.eq(id)).fetch().map { resource ->
                ActivityResourceProjection(resource[ACTIVITY_RESOURCES.RESOURCE_ID], resource[ACTIVITY_RESOURCES.QUANTITY])
            }
            ActivityDetailsProjection(id, activity[ACTIVITIES.TITLE], activity[ACTIVITIES.SUMMARY], resources)
        }
    }

    override suspend fun nextIdentity(): Int = dbQuery {
        it.nextval(ACTIVITIES_ID_SEQ).toInt()
    }

    override suspend fun activityCreated(data: ActivityCreated): Int = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .values(data.id, data.title, data.summary, data.authorId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override suspend fun getDescription(id: Int): Option<ActivityDetails> = dbQuery {
        val record = it.select(ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(record).flatMap { activity ->
            val memento = ActivityDetailsMemento(id, activity[ACTIVITIES.TITLE], activity[ACTIVITIES.SUMMARY], activity[ACTIVITIES.AUTHOR_ID])
            ActivityDetails.from(memento)
        }
    }

    override suspend fun descriptionUpdated(data: ActivityDetailsCorrected): Int = dbTransaction {
        it.update(ACTIVITIES)
                .set(ACTIVITIES.TITLE, data.title)
                .set(ACTIVITIES.SUMMARY, data.summary)
                .where(ACTIVITIES.ID.eq(data.id))
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override suspend fun getResources(id: Int): Option<ActivityResourcesRequirement> = dbQuery {
        val activityAuthor = it.select(ACTIVITIES.AUTHOR_ID).from(ACTIVITIES).where(ACTIVITIES.ID.eq(id)).fetchOne()?.get(ACTIVITIES.AUTHOR_ID)
                ?: return@dbQuery Option.empty()

        val memento = it.select(ACTIVITY_RESOURCES.RESOURCE_ID, ACTIVITY_RESOURCES.QUANTITY)
                .from(ACTIVITY_RESOURCES)
                .where(ACTIVITY_RESOURCES.ACTIVITY_ID.eq(id)).fetch().map { activityResource ->
            ActivityResource.from(activityResource[ACTIVITY_RESOURCES.RESOURCE_ID], activityResource[ACTIVITY_RESOURCES.QUANTITY]).toOption()
                }.fold(ActivityResourcesRequirementMemento(id = id, authorId = activityAuthor)) { acc, resource ->
            when (resource) {
                is Some -> {
                    acc.resources.add(resource.t)
                    acc
                }
                is None -> acc
            }
        }
        ActivityResourcesRequirement.from(memento)
    }

    override suspend fun resourceAdded(data: ActivityResourceAdded): Int = dbTransaction {
        it.insertInto(ACTIVITY_RESOURCES, ACTIVITY_RESOURCES.ACTIVITY_ID, ACTIVITY_RESOURCES.RESOURCE_ID, ACTIVITY_RESOURCES.QUANTITY)
                .values(data.id, data.resourceId, data.quantity)
                .returning(ACTIVITY_RESOURCES.ACTIVITY_ID)
                .fetchOne()[ACTIVITY_RESOURCES.ACTIVITY_ID]
    }

    override suspend fun exists(id: Int): Boolean = dbQuery {
        it.select(RESOURCES.ID).from(RESOURCES).where(RESOURCES.ID.eq(id)).fetchOne() != null
    }

    override suspend fun query(query: QueryActivitiesGateway.ActivityQueryCriteria): List<ActivitySummaryProjection> = dbQuery {
        it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .from(ACTIVITIES)
                .where(query.toCondition())
                .fetch { record ->
                    ActivitySummaryProjection(record[ACTIVITIES.ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY], record[ACTIVITIES.AUTHOR_ID])
                }
    }

    private fun QueryActivitiesGateway.ActivityQueryCriteria.toCondition(): Condition {
        var condition: Condition = DSL.trueCondition()
        val titleContains = this.titleContains
        condition = when (titleContains) {
            is Some -> condition.and(ACTIVITIES.TITLE.containsIgnoreCase(titleContains.t))
            is None -> condition
        }
        return condition
    }

}