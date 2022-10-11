package org.raccoon.ratelimit.service

import org.springframework.data.redis.core.ZSetOperations

interface RedisService {
    fun deleteScoreValue(key: String)

    fun increaseScoreValue(key: String, value: String, Increase: Double)

    fun setTimeStamp(key: String, value: String = "timestamp")

    fun isRateLimit(rateLimitSet: MutableSet<Any>?)

    fun setTimeOutData(key : String, value : Any)

    fun getSortedSet(key: String): MutableSet<ZSetOperations.TypedTuple<Any>>?
}