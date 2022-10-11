package org.raccoon.ratelimit.filter

import io.netty.util.CharsetUtil
import org.raccoon.ratelimit.Rule
import org.raccoon.ratelimit.service.RedisService
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.ByteBuffer


@Component
class RateLimitAuthFilter(
    val redisService: RedisService,
    val rule: Rule
) : AbstractGatewayFilterFactory<RateLimitAuthFilter.Config>(Config::class.java) {

    override fun apply(config: Config): GatewayFilter {
        return (label@ GatewayFilter { exchange: ServerWebExchange?, chain: GatewayFilterChain ->
            val rateLimitSet: MutableSet<ZSetOperations.TypedTuple<Any>>? = redisService.getSortedSet(rule.key)

            if(rateLimitSet != null && rateLimitSet.any { it.value.toString() == "timestamp" }){
                var sellValue: Long = -1
                rateLimitSet?.forEach {
                    if(it.value == "sell"){
                        sellValue = it.score!!.toLong()
                    }

                    if(it.value == "timestamp"){
                        val previousTimeStamp = it.score
                        val timestamp = System.currentTimeMillis()

                        if((timestamp - previousTimeStamp!!.toLong()) / (1000 * 60) > rule.max){
                            redisService.deleteScoreValue("transaction")
                            redisService.setTimeStamp(rule.key)
                            redisService.increaseScoreValue(rule.key, "buy", 0.0)
                            redisService.increaseScoreValue(rule.key, "sell", 1.0)
                        }else{
                            if(sellValue >= rule.max && sellValue != -1L){
                                return@GatewayFilter handleUnAuthorized(exchange!!)
                            }else{
                                redisService.increaseScoreValue(rule.key, "sell", 1.0)
                            }
                        }
                    }
                }
            }else {
                redisService.setTimeStamp(rule.key)
                redisService.increaseScoreValue(rule.key, "buy", 0.0)
                redisService.increaseScoreValue(rule.key, "sell", 1.0)
            }

            val request: ServerHttpRequest = exchange!!.getRequest()
            val newPath: String = request.getPath().toString() + "/change"
            val newRequest: ServerHttpRequest = request.mutate()
                .path(newPath)
                .build()
            exchange.attributes[GATEWAY_REQUEST_URL_ATTR] = newRequest.uri
            request.path
            chain.filter(exchange.mutate()
                .request(newRequest).build())})
    }

    private fun handleUnAuthorized(exchange: ServerWebExchange): Mono<Void?>? {
        val response = exchange.response
        response.statusCode = HttpStatus.TOO_MANY_REQUESTS
        val value = "Too Many Requests"
        val byteBuffer = ByteBuffer.wrap(value.toByteArray(CharsetUtil.UTF_8))
        val dataBuffer: DataBuffer = DefaultDataBufferFactory().wrap(byteBuffer)
        response.writeWith(Mono.just(dataBuffer))
        return response.setComplete()
    }

    class Config
}