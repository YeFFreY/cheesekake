package org.yeffrey.cheesekake.web

import arrow.core.Option
import org.yeffrey.cheesekake.api.usecase.Principal
import org.yeffrey.cheesekake.api.usecase.UseCaseContext

class WebContext<R>(override val request: R, override val principal: Option<Principal> = Option.empty()) : UseCaseContext<R>
