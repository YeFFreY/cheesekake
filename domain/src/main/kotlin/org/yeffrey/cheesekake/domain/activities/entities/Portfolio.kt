package org.yeffrey.cheesekake.domain.activities.entities

import arrow.core.Either
import org.yeffrey.cheesekake.domain.Event
import org.yeffrey.cheesekake.domain.ValidationError
import org.yeffrey.cheesekake.domain.toDomainString

data class ActivityCreated(val id: Int, val title: String, val summary: String, val authorId: Int) : Event
data class ActivityDetailsCorrected(val id: Int, val title: String, val summary: String) : Event
data class ActivityResourceAdded(val id: Int, val resourceId: Int, val quantity: Int) : Event
data class ActivityResourceRemoved(val id: Int, val resourceId: Int) : Event

enum class TrainingLevel {
    Low,
    Normal,
    Hard
}

//fun main(args: Array<String>) {
//    val rn = ResourceName.from("")
//    val rd = ResourceDescription.from("")
//    val r = validate(rn, rd) { a, b ->
//        Resource(1, a, b)
//    }
//    println(r)
//}


data class ResourceName internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ResourceName> = value.toDomainString(250, ValidationError.InvalidResourceName, ::ResourceName)
    }
}

data class ResourceDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, ResourceDescription> = value.toDomainString(5000, ValidationError.InvalidResourceDescription, ::ResourceDescription)
    }
}

data class Resource constructor(val id: Int, val name: ResourceName, val description: ResourceDescription)

data class SkillName internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, SkillName> = value.toDomainString(250, ValidationError.InvalidSkillName, ::SkillName)
    }
}

data class SkillDescription internal constructor(val value: String) {
    companion object {
        fun from(value: String): Either<ValidationError, SkillDescription> = value.toDomainString(5000, ValidationError.InvalidSkillDescription, ::SkillDescription)
    }
}

data class Skill constructor(val id: Int, val name: SkillName, val description: SkillDescription)

data class TrainedSkill constructor(val skillId: Int) {
    val level = TrainingLevel.Normal
}

data class ActivityTrainedSkills constructor(val id: Int, val skills: Set<TrainedSkill> = emptySet())

data class RequiredSkill constructor(val skillId: Int) {
    val level = TrainingLevel.Normal
}

data class ActivitySkillsRequirement(val id: Int, val skills: Set<RequiredSkill> = emptySet())


