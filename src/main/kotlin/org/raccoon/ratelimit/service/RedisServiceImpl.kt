package org.raccoon.ratelimit.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisServiceImpl(val redisTemplate : RedisTemplate<String, Any>) : RedisService
{
    var timeout : String = "3"
    var redisSetTemplate = redisTemplate.opsForZSet()
    var redisValueTemplate = redisTemplate.opsForValue()

    override fun getSortedSet(key: String): MutableSet<ZSetOperations.TypedTuple<Any>>? = redisSetTemplate.rangeWithScores(key,0, -1)

    override fun increaseScoreValue(key: String, value: String, Increase: Double){
        redisSetTemplate.incrementScore(key, value, Increase)
    }

    override fun deleteScoreValue(key: String){
        redisSetTemplate.removeRange(key, 0, -1)
    }

    fun addSet(key: String, value: String, score: Double){
        redisSetTemplate.add(key, value, score)
    }

    override fun setTimeStamp(key: String, value: String){
        val timestamp = System.currentTimeMillis()
        addSet(key, value, timestamp.toDouble())
    }

    override fun isRateLimit(rateLimitSet: MutableSet<Any>?){
        val timeStamp = System.currentTimeMillis()
    }

    override fun setTimeOutData(key: String, value: Any) {
        redisValueTemplate.set(key, value, timeout.toLong(), TimeUnit.MINUTES)
    }
}