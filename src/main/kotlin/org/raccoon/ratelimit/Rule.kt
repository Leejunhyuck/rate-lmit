package org.raccoon.ratelimit

import org.springframework.stereotype.Component

@Component
data class Rule(
    var key: String = "transaction",
    var ttl: String = "",
    var max: Long = 3
)