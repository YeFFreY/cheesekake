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
import org.yeffrey.cheesekake.persistence.db.Tables.ACTIVITIES

class ActivityGatewayImpl : CreateActivityGateway, UpdateActivityGateway, QueryActivityGateway, AddResourcesActivityGateway {
    override suspend fun activityCreated(data: ActivityCreated): ActivityId = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .values(data.title, data.summary, data.authorId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override suspend fun getDescription(id: ActivityId): Option<Activity> = dbQuery {
        it.select(ACTIVITIES.ID, ACTIVITIES.AUTHOR_ID, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY)
                .from(ACTIVITIES)
                .where(ACTIVITIES.ID.eq(id))
                .fetchOne().map { record ->
                    val memento = ActivityMemento(record[ACTIVITIES.ID], record[ACTIVITIES.AUTHOR_ID], record[ACTIVITIES.TITLE], record[ACTIVITIES.SUMMARY])
                    Activity.from(memento)
                }
    }

    override suspend fun descriptionUpdated(data: ActivityDescriptionUpdated): ActivityId {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getResources(id: ActivityId): Option<Activity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun resourceAdded(data: ActivityResourceAdded): ActivityId {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /*override suspend fun getResources(id: ActivityId): Option<Activity> = dbQuery { dslContext ->
        val resourceIds = dslContext.select(ACTIVITY_RESOURCES.RESOURCE_ID, ACTIVITIES.AUTHOR_ID)
                .from(ACTIVITIES.join(ACTIVITY_RESOURCES).on(ACTIVITIES.ID.eq(ACTIVITY_RESOURCES.ACTIVITY_ID)))
                .where(ACTIVITIES.ID.eq(id))
                .fetch()
        if (resourceIds.isEmpty()) {
            return@dbQuery Option.empty()
        }
        return@dbQuery Activity(id.toOption(), Writer(resourceIds.first()[ACTIVITIES.AUTHOR_ID]), ActivityresourceIds.map { it[ACTIVITY_RESOURCES.RESOURCE_ID] }).toOption()

    }

    override suspend fun updateResources(resources: ActivityResources): ActivityId = dbTransaction { dslContext ->
        dslContext.delete(ACTIVITY_RESOURCES).where(ACTIVITY_RESOURCES.RESOURCE_ID.notIn(resources.resources)).execute()
        val existingResources = dslContext.selectFrom(ACTIVITY_RESOURCES).where(ACTIVITY_RESOURCES.ACTIVITY_ID.eq(resources.id.orNull())).fetch()
        existingResources.addAll(resources.resources.map { ActivityResourcesRecord(resources.id.orNull(), it) })
        dslContext.batchStore(existingResources).execute()
        resources.id.get()
    }

    override suspend fun getDescription(id: ActivityId): Option<ActivityDescription> = dbQuery {
        val record = it.fetchOne(ACTIVITIES, ACTIVITIES.ID.eq(id))
        Option.fromNullable(record).map { activity ->
            ActivityDescription(activity[ACTIVITIES.ID].toOption(), Writer(activity[ACTIVITIES.AUTHOR_ID]), activity[ACTIVITIES.TITLE].activityTitle().getOrElse { ActivityTitle.invalid(activity[ACTIVITIES.TITLE]) }, activity[ACTIVITIES.SUMMARY])
        }
    }

    override suspend fun create(activityBase: ActivityDescription): Int = dbQuery {
        it.insertInto(ACTIVITIES, ACTIVITIES.TITLE, ACTIVITIES.SUMMARY, ACTIVITIES.AUTHOR_ID)
                .values(activityBase.title.value, activityBase.summary, activityBase.writer.userId)
                .returning(ACTIVITIES.ID)
                .fetchOne()[ACTIVITIES.ID]
    }

    override suspend fun updateDescription(activityBase: ActivityDescription): ActivityId = dbQuery {
        it.update(ACTIVITIES)
                .set(ACTIVITIES.TITLE, activityBase.title.value)
                .set(ACTIVITIES.SUMMARY, activityBase.summary)
                .where(ACTIVITIES.ID.eq(activityBase.id.orNull()))
                .returning(ACTIVITIES.ID)
                .fetchOne().getValue(ACTIVITIES.ID)
    }*/

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