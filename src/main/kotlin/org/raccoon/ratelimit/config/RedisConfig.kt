package org.raccoon.ratelimit.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfig() {

    @Bean
    fun redisTemplate(connectionFactory : RedisConnectionFactory) : RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.keySerializer = StringRedisSerializer() //key 깨짐 방지
        redisTemplate.valueSerializer = StringRedisSerializer() //value 깨짐 방지
        redisTemplate.setConnectionFactory(connectionFactory)
        return redisTemplate
    }
}