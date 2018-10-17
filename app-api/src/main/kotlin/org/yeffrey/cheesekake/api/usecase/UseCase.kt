package org.yeffrey.cheesekake.api.usecase

import arrow.core.Option

abstract class UseCaseRequest {
    var userId : Option<Int> = Option.empty()
}