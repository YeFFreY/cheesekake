package org.yeffrey.cheesekake.api.usecase.activities

import arrow.core.Option
import io.kotlintest.Description
import io.kotlintest.specs.StringSpec
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.yeffrey.cheesekake.domain.activities.CreateActivityGateway

class CreateActivityImplTest : StringSpec() {
    val gatewayMock = mockk<CreateActivityGateway>()
    val presenterSpy = spyk<CreateActivity.Presenter>()

    override fun beforeTest(description: Description) {
        clearMocks(gatewayMock)
        clearMocks(presenterSpy)
    }

    init {
        "Presenter access denied called when no user given" {
            val subject = CreateActivityImpl(gatewayMock)
            runBlocking {
                subject.handle(CreateActivity.Request("title", "summary"), presenterSpy)
                coVerify(exactly = 1) { presenterSpy.accessDenied() }
                coVerify(exactly = 0) { presenterSpy.success(any()) }
                coVerify(exactly = 0) { gatewayMock.nextIdentity() }
            }
        }

        "Presenter validation failed called when invalid title/summary" {
            val subject = CreateActivityImpl(gatewayMock)
            coEvery { gatewayMock.nextIdentity() } returns 1 // TODO change code of use case to avoid wasting sequence when data invalid...
            runBlocking {
                subject.handle(CreateActivity.Request("", ""), presenterSpy, Option.just(1))
                coVerify(exactly = 1) { presenterSpy.validationFailed(any()) }
                coVerify(exactly = 0) { presenterSpy.success(any()) }
            }
        }

        "Presenter success called when valid title/summary" {
            val subject = CreateActivityImpl(gatewayMock)
            coEvery { gatewayMock.nextIdentity() } returns 1
            coEvery { gatewayMock.activityCreated(any()) } returns 1
            runBlocking {
                subject.handle(CreateActivity.Request("title", "summary"), presenterSpy, Option.just(1))
                coVerify(exactly = 1) { presenterSpy.success(any()) }
                coVerify(exactly = 1) { gatewayMock.activityCreated(any()) }
                coVerify(exactly = 0) { presenterSpy.validationFailed(any()) }
            }
        }

    }
}