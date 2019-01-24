package org.yeffrey.cheesekake.domain.skills

import arrow.core.Option

data class Skill(val id: Int, val name: String, val description: Option<String>)