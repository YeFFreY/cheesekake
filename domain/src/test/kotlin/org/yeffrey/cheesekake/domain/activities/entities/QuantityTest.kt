package org.yeffrey.cheesekake.domain.activities.entities

import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.specs.StringSpec
import org.yeffrey.cheesekake.domain.ValidationError

class QuantityTest : StringSpec() {
    init {
        "Should build Valid Quantity" {
            assertAll(Gen.positiveIntegers()) { a ->
                val result = Quantity.from(a)
                result.shouldBeRight()
                result.map { it.value }.shouldBeRight(a)
            }
        }
        "Should return error with negative value" {
            assertAll(Gen.negativeIntegers()) { a ->
                val result = Quantity.from(a)
                result.shouldBeLeft(ValidationError.InvalidQuantity)
            }
        }
        "Should return error with zero" {
            Quantity.from(0).shouldBeLeft(ValidationError.InvalidQuantity)
        }
    }
}
