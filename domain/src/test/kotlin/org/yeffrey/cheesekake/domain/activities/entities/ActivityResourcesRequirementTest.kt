package org.yeffrey.cheesekake.domain.activities.entities

import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.fail
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.yeffrey.cheesekake.domain.ValidationError

class ActivityResourceGenerator : Gen<ActivityResource> {
    override fun constants() = emptyList<ActivityResource>()
    override fun random() = generateSequence {
        ActivityResource(Gen.positiveIntegers().random().first(), Quantity(1))
    }
}

class ActivityResourcesRequirementTest : StringSpec() {
    init {
        "Should rehydrate with empty list of resource" {
            val memento = ActivityResourcesRequirementMemento(id = 1, authorId = 1)
            val result = ActivityResourcesRequirement.from(memento)
            result.fold({ fail("Activity Resource Requirements should have been rehydrated") }) {
                it.id.shouldBe(1)
                it.resources.shouldBeEmpty()
                it.authorId.shouldBe(1)
            }
        }
        "Should rehydrate with list of resources" {
            assertAll(Gen.list(ActivityResourceGenerator()).filterNot { it.isEmpty() }) { resources: List<ActivityResource> ->
                val memento = ActivityResourcesRequirementMemento(id = 1, authorId = 1, resources = resources.toMutableSet())
                val result = ActivityResourcesRequirement.from(memento)
                result.fold({ fail("Activity Resource Requirements should have been rehydrated") }) {
                    it.id.shouldBe(1)
                    it.resources.shouldHaveSize(resources.size)
                    it.authorId.shouldBe(1)
                }
            }
        }
        "Should add a resources to existing list of resources" {
            assertAll(Gen.list(ActivityResourceGenerator()).filterNot { it.isEmpty() }) { resources: List<ActivityResource> ->
                val memento = ActivityResourcesRequirementMemento(id = 1, authorId = 1, resources = resources.toMutableSet())
                val original = ActivityResourcesRequirement.from(memento)
                original.fold({ fail("Activity Resource Requirements should have been rehydrated") }) { activityResourcesRequirementTest ->
                    val result = activityResourcesRequirementTest.add(ActivityResource(1, Quantity(10)))
                    result.map { it.result.resources.shouldHaveSize(resources.size + 1) }
                    result.map { it.event.resourceId.shouldBe(1) }
                    result.map { it.event.quantity.shouldBe(10) }
                }
            }
        }
        "Should add a resources to existing empty list of resources" {
            assertAll(ActivityResourceGenerator()) { resource: ActivityResource ->
                val memento = ActivityResourcesRequirementMemento(id = 1, authorId = 1)
                val original = ActivityResourcesRequirement.from(memento)
                original.fold({ fail("Activity Resource Requirements should have been rehydrated") }) { activityResourcesRequirementTest ->
                    val result = activityResourcesRequirementTest.add(resource)
                    result.map { it.result.resources.shouldHaveSize(1) }
                    result.map { it.event.resourceId.shouldBe(resource.resourceId) }
                    result.map { it.event.quantity.shouldBe(resource.quantity.value) }
                }
            }
        }
        "Should not add a resource which is already in the list" {
            val memento = ActivityResourcesRequirementMemento(id = 1, authorId = 1, resources = mutableSetOf(ActivityResource(1, Quantity(10))))
            val original = ActivityResourcesRequirement.from(memento)
            original.fold({ fail("Activity Resource Requirements should have been rehydrated") }) { activityResourcesRequirementTest ->
                original.map { it.resources.shouldHaveSize(1) }
                val result = activityResourcesRequirementTest.add(ActivityResource(1, Quantity(10)))
                result.shouldBeLeft(listOf(ValidationError.DuplicateActivityResource))
                original.map { it.resources.shouldHaveSize(1) }
            }

        }

        "Should remove a resource from existing list of resources" {
            assertAll(Gen.list(ActivityResourceGenerator()).filterNot { it.isEmpty() }) { resources: List<ActivityResource> ->
                val memento = ActivityResourcesRequirementMemento(id = 1, authorId = 1, resources = resources.toMutableSet())
                val original = ActivityResourcesRequirement.from(memento)
                original.fold({ fail("Activity Resource Requirements should have been rehydrated") }) { activityResourcesRequirementTest ->
                    val result = activityResourcesRequirementTest.remove(resources[0].resourceId)
                    result.map { it.result.resources.shouldHaveSize(resources.size - 1) }
                    result.map { it.event.resourceId.shouldBe(resources[0].resourceId) }
                }
            }
        }
        "Should return error when removing an unknown resource from existing list of resources" {
            assertAll(Gen.list(ActivityResourceGenerator()).filterNot { it.isEmpty() }) { resources: List<ActivityResource> ->
                val memento = ActivityResourcesRequirementMemento(id = 1, authorId = 1, resources = resources.toMutableSet())
                val original = ActivityResourcesRequirement.from(memento)
                original.fold({ fail("Activity Resource Requirements should have been rehydrated") }) { activityResourcesRequirementTest ->
                    val result = activityResourcesRequirementTest.remove(-1)
                    original.map { it.resources.shouldHaveSize(resources.size) }
                    result.shouldBeLeft(listOf(ValidationError.UnknownActivityResource))
                }
            }
        }

    }
}