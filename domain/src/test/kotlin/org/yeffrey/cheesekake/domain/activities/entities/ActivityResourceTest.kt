package org.yeffrey.cheesekake.domain.activities.entities

import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.specs.StringSpec
import org.yeffrey.cheesekake.domain.ValidationError

class ActivityResourceTest : StringSpec() {
    init {
        "Should build when Valid Quantity" {
            assertAll(Gen.positiveIntegers()) { qty ->
                val result = ActivityResource.from(1, qty)
                result.shouldBeRight()
                result.map { it.quantity.value }.shouldBeRight(qty)
                result.map { it.resourceId }.shouldBeRight(1)
            }
        }
        "Should return error with negative value" {
            assertAll(Gen.negativeIntegers()) { qty ->
                val result = ActivityResource.from(1, qty)
                result.shouldBeLeft(listOf(ValidationError.InvalidQuantity))
            }
        }
        "Should return error with zero" {
            ActivityResource.from(1, 0).shouldBeLeft(listOf(ValidationError.InvalidQuantity))
        }
    }
}
