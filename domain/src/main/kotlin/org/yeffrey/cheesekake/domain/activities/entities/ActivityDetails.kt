package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Either
import arrow.core.Option
import arrow.validation.validate
import org.yeffrey.cheesekake.domain.CommandResult
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.activities.Activity
import org.yeffrey.cheesekake.domain.toDomainString


data class ActivityTitle internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ActivityTitle> = value.toDomainString(250, ValidationError.InvalidActivityTitle, ::ActivityTitle)
    }
}

data class ActivitySummary internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ActivitySummary> = value.toDomainString(250, ValidationError.InvalidActivitySummary, ::ActivitySummary)
    }
}

data class ActivityDetailsMemento(val id: Int, val title: String, val summary: String, val authorId: Int)
data class ActivityDetails internal constructor(val id: Int, val title: ActivityTitle, val summary: ActivitySummary, override val authorId: Int) : Activity {

    companion object {
        private fun build(title: String, summary: String, block: (ActivityTitle, ActivitySummary) -> ActivityDetails): Either<List<ValidationError>, ActivityDetails> {
            val t = ActivityTitle.from(title)
            val s = ActivitySummary.from(summary)
            return validate(t, s, block)
        }

        fun from(memento: ActivityDetailsMemento): Option<ActivityDetails> {
            return ActivityDetails.build(memento.title, memento.summary) { title, summary ->
                ActivityDetails(memento.id, title, summary, memento.authorId)
            }.toOption()
        }

        fun new(id: Int, title: String, summary: String, authorId: Int): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityCreated>> {
            return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
                ActivityDetails(id, titleValue, summaryValue, authorId)
            }.map {
                CommandResult(it, ActivityCreated(it.id, it.title.value, it.summary.value, it.authorId))
            }
        }

    }

    fun updateActivityDetails(title: String, summary: String): Either<List<ValidationError>, CommandResult<ActivityDetails, ActivityDetailsCorrected>> {
        return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
            this.copy(title = titleValue, summary = summaryValue)
        }.map {
            CommandResult(it, ActivityDetailsCorrected(it.id, it.title.value, it.summary.value))
        }
    }
}
