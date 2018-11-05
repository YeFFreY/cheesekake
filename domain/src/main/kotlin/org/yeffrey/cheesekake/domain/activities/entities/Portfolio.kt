package org.yeffrey.cheesekake.domain.activities.entities

import arrow.data.*
import org.yeffrey.cheesekake.domain.NonEmptySet
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.toDomainString

enum class TrainingLevel {
    Low,
    Normal,
    Hard
}

data class ResourceName internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ResourceName> = value.toDomainString(250, ValidationError.InvalidResourceName, ::ResourceName)
    }
}

data class ResourceDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ResourceDescription> = value.toDomainString(5000, ValidationError.InvalidResourceDescription, ::ResourceDescription)
    }
}

data class Resource constructor(val id: Int, val name: ResourceName, val description: ResourceDescription)

data class SkillName internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, SkillName> = value.toDomainString(250, ValidationError.InvalidSkillName, ::SkillName)
    }
}

data class SkillDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, SkillDescription> = value.toDomainString(5000, ValidationError.InvalidSkillDescription, ::SkillDescription)
    }
}

data class Skill constructor(val id: Int, val name: SkillName, val description: SkillDescription)

data class ActivityTitleTwo internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ActivityTitleTwo> = value.toDomainString(250, ValidationError.InvalidActivityTitle, ::ActivityTitleTwo)
    }
}

data class ActivitySummary internal constructor(val value: String) {
    companion object {
        fun from(value: String): ValidatedNel<ValidationError, ActivitySummary> = value.toDomainString(250, ValidationError.InvalidActivitySummary, ::ActivitySummary)
    }
}

data class ActivityDetails internal constructor(val id: Int, val title: ActivityTitleTwo, val summary: ActivitySummary) {
    companion object {
        fun build(title: String, summary: String, block: (ActivityTitleTwo, ActivitySummary) -> ActivityDetails): ValidatedNel<ValidationError, ActivityDetails> {
            return Validated.applicative(Nel.semigroup<ValidationError>()).map(
                    ActivityTitleTwo.from(title),
                    ActivitySummary.from(summary)
            ) { (titleValue, summaryValue) ->
                block(titleValue, summaryValue)
            }.fix()
        }

        fun new(id: Int, title: String, summary: String): ValidatedNel<ValidationError, ActivityDetails> {
            return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
                ActivityDetails(id, titleValue, summaryValue)
            }
        }
    }

    fun updateActivityDetails(activityDetails: ActivityDetails, title: String, summary: String): ValidatedNel<ValidationError, ActivityDetails> {
        return ActivityDetails.build(title, summary) { titleValue, summaryValue ->
            activityDetails.copy(title = titleValue, summary = summaryValue)
        }
    }
}

data class ActivityResource constructor(val resource: Resource) {
    var qty: Int = 0
}

data class ActivityResourcesRequirement constructor(val id: Int, val resources: NonEmptySet<ActivityResource>)

data class TrainedSkill constructor(val skill: Skill) {
    var level = TrainingLevel.Normal
}

data class ActivityTrainedSkills constructor(val id: Int, val skills: NonEmptySet<TrainedSkill>)

data class RequiredSkill constructor(val skill: Skill) {
    var level = TrainingLevel.Normal
}

data class ActivitySkillsRequirement(val id: Int, val skills: NonEmptySet<RequiredSkill>)


