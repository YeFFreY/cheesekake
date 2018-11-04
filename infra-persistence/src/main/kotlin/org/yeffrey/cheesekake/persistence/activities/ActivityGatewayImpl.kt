package org.yeffrey.cheesekake.persistence.activities

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.jooq.Condition
import org.jooq.impl.DSL
import org.yeffrey.cheesekake.domain.activities.*
import org.yeffrey.cheesekake.domain.activities.entities.*
import org.yeffrey.cheesekake.domain.activities.query.ActivityDetails
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Sequences.ACTIVITIES_ID_SEQ
import org.yeffrey.cheesekake.persistence.db.Tables.*

class ActivityGatewayImpl : CreateActivityGateway, UpdateActivityGateway, QueryActivitiesGateway, QueryActivityGateway, AddResourcesActivityGateway {
    override suspend fun get(id: Int): Option<ActivityDetails> = dbQuery {
        val record = it.select(ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(record).map { activity ->
            val resourceIds = it.select(ACTIVITY_RESOURCES.RESOURCE_ID).from(ACTIVITY_RESOURCES).where(ACTIVITY_RESOURCES.ACTIVITY_ID.eq(id)).fetch().map { resource ->
                resource[ACTIVITY_RESOURCES.RESOURCE_ID]
            }
            ActivityDetails(id, activity[ACTIVITIES.TITLE], activity[ACTIVITIES.SUMMARY], resourceIds)
        }
    }

    override suspend fun nextIdentity(): ActivityId = dbQuery {
        it.nextval(ACTIVITIES_ID_SEQ).toInt()
    }

    override suspend fun activityCreated(data: ActivityCreated): ActivityId = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .values(data.id, data.title, data.summary)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override suspend fun getDescription(id: ActivityId): Option<Activity> = dbQuery {
        val record = it.select(ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(record).flatMap { activity ->
            val memento = ActivityMemento(id, activity[ACTIVITIES.TITLE], activity[ACTIVITIES.SUMMARY])
            Activity.from(memento)
        }
    }

    override suspend fun descriptionUpdated(data: ActivityDescriptionUpdated): ActivityId = dbTransaction {
        it.update(ACTIVITIES)
                .set(ACTIVITIES.TITLE, data.title)
                .set(ACTIVITIES.SUMMARY, data.summary)
                .where(ACTIVITIES.ID.eq(data.id))
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override suspend fun getResources(id: ActivityId): Option<Activity> = dbQuery {
        val record = it.select(ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(record).flatMap { activity ->
            val resourceIds = it.select(ACTIVITY_RESOURCES.RESOURCE_ID).from(ACTIVITY_RESOURCES).where(ACTIVITY_RESOURCES.ACTIVITY_ID.eq(id)).fetch().map { resource ->
                resource[ACTIVITY_RESOURCES.RESOURCE_ID]
            }
            val memento = ActivityMemento(id, activity[ACTIVITIES.TITLE], activity[ACTIVITIES.SUMMARY], resourceIds.toSet())
            Activity.from(memento)
        }
    }

    override suspend fun resourceAdded(data: ActivityResourceAdded): ActivityId = dbTransaction {
        it.insertInto(ACTIVITY_RESOURCES, ACTIVITY_RESOURCES.ACTIVITY_ID, ACTIVITY_RESOURCES.RESOURCE_ID)
                .values(data.id, data.resourceId)
                .returning(ACTIVITY_RESOURCES.ACTIVITY_ID)
                .fetchOne()[ACTIVITY_RESOURCES.ACTIVITY_ID]
    }

    override suspend fun exists(id: ResourceId): Boolean = dbQuery {
        it.select(RESOURCES.ID).from(RESOURCES).where(RESOURCES.ID.eq(id)).fetchOne() != null
    }

    override suspend fun query(query: QueryActivitiesGateway.ActivityQueryCriteria): List<ActivitySummary> = dbQuery {
        it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(query.toCondition())
                .fetch { record ->
                    ActivitySummary(record[ACTIVITIES.ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
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