package org.yeffrey.cheesekake.persistence.activities

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import org.jooq.Condition
import org.jooq.impl.DSL
import org.yeffrey.cheesekake.domain.activities.AddResourcesActivityGateway
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway
import org.yeffrey.cheesekake.domain.activities.QueryActivityGateway
import org.yeffrey.cheesekake.domain.activities.UpdateActivityGateway
import org.yeffrey.cheesekake.domain.activities.entities.*
import org.yeffrey.cheesekake.domain.activities.query.ActivitySummary
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Sequences.ACTIVITIES_ID_SEQ
import org.yeffrey.cheesekake.persistence.db.Tables.*

class ActivityGatewayImpl : CreateActivityGateway, UpdateActivityGateway, QueryActivityGateway, AddResourcesActivityGateway {
    override suspend fun nextIdentity(): ActivityId = dbQuery {
        it.nextval(ACTIVITIES_ID_SEQ).toInt()
    }

    override suspend fun activityCreated(data: ActivityCreated): ActivityId = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .values(data.id, data.title, data.summary, data.authorId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override suspend fun getDescription(id: ActivityId): Option<Activity> = dbQuery {
        val record = it.select(ACTIVITIES.AUTHOR_ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(record).flatMap { record ->
            val memento = ActivityMemento(id, record[ACTIVITIES.AUTHOR_ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
            Activity.from(memento)
        }
    }

    override suspend fun descriptionUpdated(data: ActivityDescriptionUpdated): ActivityId {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getResources(id: ActivityId): Option<Activity> = dbQuery {
        val record = it.select(ACTIVITIES.AUTHOR_ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne()
        Option.fromNullable(record).flatMap { activity ->
            val resourceIds = it.select(ACTIVITY_RESOURCES.RESOURCE_ID).from(ACTIVITY_RESOURCES).where(ACTIVITY_RESOURCES.ACTIVITY_ID.eq(id)).fetch().map { resource ->
                resource[ACTIVITY_RESOURCES.RESOURCE_ID]
            }
            val memento = ActivityMemento(id, activity[ACTIVITIES.AUTHOR_ID], activity[ACTIVITIES.TITLE], activity[ACTIVITIES.SUMMARY], resourceIds.toSet())
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

    override suspend fun query(query: QueryActivityGateway.ActivityQueryCriteria): List<ActivitySummary> = dbQuery {
        it.select(ACTIVITIES.ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(query.toCondition())
                .fetch { record ->
                    ActivitySummary(record[ACTIVITIES.ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
                }
    }

    private fun QueryActivityGateway.ActivityQueryCriteria.toCondition(): Condition {
        var condition: Condition = DSL.trueCondition()
        val titleContains = this.titleContains
        condition = when (titleContains) {
            is Some -> condition.and(ACTIVITIES.TITLE.containsIgnoreCase(titleContains.t))
            is None -> condition
        }
        return condition
    }

}