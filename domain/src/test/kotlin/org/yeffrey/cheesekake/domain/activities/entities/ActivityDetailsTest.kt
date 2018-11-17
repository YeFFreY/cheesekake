package org.yeffrey.cheesekake.domain.activities.entities

import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.assertions.arrow.option.shouldBeNone
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.yeffrey.cheesekake.domain.ValidationError

fun notBlankString(maxLength: Int) = Gen.string().filter { it.trim().length in 1..maxLength }
fun unexpectedString(maxLength: Int) = Gen.string().filter { it.trim().length !in 1..maxLength }

class ActivityTitleTest : StringSpec() {
    init {
        "Should build Valid title" {
            assertAll(notBlankString(ActivityTitle.MAX_SIZE)) { a: String ->
                val result = ActivityTitle.from(a)
                result.shouldBeRight()
                result.map { it.value }.shouldBeRight(a)
            }
        }
        "Should return error" {
            assertAll(unexpectedString(ActivityTitle.MAX_SIZE)) { a: String ->
                val result = ActivityTitle.from(a)
                result.shouldBeLeft(ValidationError.InvalidActivityTitle)
            }
        }
    }
}

class ActivitySummaryTest : StringSpec() {
    init {
        "Should build Valid summary" {
            assertAll(notBlankString(ActivitySummary.MAX_SIZE)) { a: String ->
                val result = ActivitySummary.from(a)
                result.shouldBeRight()
                result.map { it.value }.shouldBeRight(a)
            }
        }
        "Should return error" {
            assertAll(unexpectedString(ActivitySummary.MAX_SIZE)) { a: String ->
                val result = ActivitySummary.from(a)
                result.shouldBeLeft(ValidationError.InvalidActivitySummary)
            }
        }
    }
}

class ActivityDetailsTest : StringSpec() {
    init {
        "Should build Activity details" {
            assertAll(notBlankString(ActivityTitle.MAX_SIZE), notBlankString(ActivitySummary.MAX_SIZE)) { title: String, summary: String ->
                val result = ActivityDetails.new(1, title, summary, 1)
                result.shouldBeRight()
                result.map { it.result.title.value }.shouldBeRight(title)
                result.map { it.result.summary.value }.shouldBeRight(summary)
                result.map { it.event }.shouldBeRight(ActivityCreated(1, title, summary, 1))
            }
        }
        "Should return error for both title and summary" {
            assertAll(unexpectedString(ActivityTitle.MAX_SIZE), unexpectedString(ActivitySummary.MAX_SIZE)) { title: String, summary: String ->
                val result = ActivityDetails.new(1, title, summary, 1)
                result.shouldBeLeft()
                result.shouldBeLeft(listOf(ValidationError.InvalidActivityTitle, ValidationError.InvalidActivitySummary))
            }
        }
        "Should rehydrate given valid memento" {
            assertAll(notBlankString(ActivityTitle.MAX_SIZE), notBlankString(ActivitySummary.MAX_SIZE)) { title, summary ->
                val original = ActivityDetails.from(ActivityDetailsMemento(1, title, summary, 1))
                original.fold({ fail("Activity Details not rehydrated") }) { activityDetails ->
                    activityDetails.title.value.shouldBe(title)
                    activityDetails.summary.value.shouldBe(summary)
                    activityDetails.id.shouldBe(1)
                    activityDetails.authorId.shouldBe(1)
                }
            }
        }
        "Should not rehydrate given invalid memento" {
            assertAll(unexpectedString(ActivityTitle.MAX_SIZE), unexpectedString(ActivitySummary.MAX_SIZE)) { title, summary ->
                val original = ActivityDetails.from(ActivityDetailsMemento(1, title, summary, 1))
                original.shouldBeNone()
            }
        }
        "Should update the title and the summary" {
            assertAll(notBlankString(ActivityTitle.MAX_SIZE), notBlankString(ActivitySummary.MAX_SIZE)) { title: String, summary: String ->
                val original = ActivityDetails.from(ActivityDetailsMemento(1, "title", "summary", 1))
                original.fold({ fail("Activity details was not created from memento") }) { activityDetails ->
                    val result = activityDetails.update(title, summary)
                    result.map { it.result.title.value }.shouldBeRight(title)
                    result.map { it.result.summary.value }.shouldBeRight(summary)
                    result.map { it.event }.shouldBeRight(ActivityDetailsCorrected(1, title, summary))
                }
            }
        }
        "Should not update when new title value invalid" {
            assertAll(unexpectedString(ActivityTitle.MAX_SIZE), notBlankString(ActivitySummary.MAX_SIZE)) { title: String, summary: String ->
                val original = ActivityDetails.from(ActivityDetailsMemento(1, "title", "summary", 1))
                original.fold({ fail("Activity details was not created from memento") }) { activityDetails ->
                    val result = activityDetails.update(title, summary)
                    activityDetails.title.value.shouldBe("title")
                    activityDetails.summary.value.shouldBe("summary")
                    result.shouldBeLeft(listOf(ValidationError.InvalidActivityTitle))
                }
            }
        }
        "Should not update when new summary value invalid" {
            assertAll(notBlankString(ActivityTitle.MAX_SIZE), unexpectedString(ActivitySummary.MAX_SIZE)) { title: String, summary: String ->
                val original = ActivityDetails.from(ActivityDetailsMemento(1, "title", "summary", 1))
                original.fold({ fail("Activity details was not created from memento") }) { activityDetails ->
                    val result = activityDetails.update(title, summary)
                    activityDetails.title.value.shouldBe("title")
                    activityDetails.summary.value.shouldBe("summary")
                    result.shouldBeLeft(listOf(ValidationError.InvalidActivitySummary))
                }
            }
        }
        "Should not update when new title and summary values invalid" {
            assertAll(unexpectedString(ActivityTitle.MAX_SIZE), unexpectedString(ActivitySummary.MAX_SIZE)) { title: String, summary: String ->
                val original = ActivityDetails.from(ActivityDetailsMemento(1, "title", "summary", 1))
                original.fold({ fail("Activity details was not created from memento") }) { activityDetails ->
                    val result = activityDetails.update(title, summary)
                    activityDetails.title.value.shouldBe("title")
                    activityDetails.summary.value.shouldBe("summary")
                    result.shouldBeLeft(listOf(ValidationError.InvalidActivityTitle, ValidationError.InvalidActivitySummary))
                }
            }
        }

    }
}

